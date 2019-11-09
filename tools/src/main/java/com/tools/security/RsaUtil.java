package com.tools.security;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Administrator on 2019/4/25.
 * 类描述:Rsa加密解密工具类
 * Rsa(对称加密)，适合加密少量数据(公钥加密，私钥解密)
 */

public class RsaUtil {
    public static final String RSA = "RSA";

    // 加密方式，android的
    //public static final String TRANSFORMATION = "RSA/None/NoPadding";
    //加密方式，标准jdk的
    //public static final String TRANSFORMATION = "RSA/None/PKCS1Padding";

    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static String sTransform = "RSA/None/PKCS1Padding";

    public static final int Default_Key_Size = 2048;//秘钥默认长度
    public static final byte[] Default_Split = "#PART#".getBytes();    // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
    public static final int Default_BufferSize = (Default_Key_Size / 8) - 11;// 当前秘钥支持加密的最大字节数

    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int sBase64Mode = Base64.DEFAULT;

    //构造方法私有，防止外部实例化
    private RsaUtil() {
    }

    public static RsaUtil getInstance() {
        return RsaUtilClassHolder.rsaUtil;
    }

    private static class RsaUtilClassHolder {
        private static final RsaUtil rsaUtil = new RsaUtil();
    }

    //初始化方法，修改默认参数
    public void init(String transform, int base64Mode) {
        sTransform = transform;
        sBase64Mode = base64Mode;
    }

    /**
     * 产生密钥对
     *
     * @param keyLength 密钥长度，小于1024长度的密钥已经被证实是不安全的，通常设置为1024或者2048，建议2048
     */
    public KeyPair generateRSAKeyPair(int keyLength) {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            //设置密钥长度
            keyPairGenerator.initialize(keyLength);
            //产生密钥对
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * 加密或解密数据的通用方法
     *
     * @param srcData 待处理的数据
     * @param key     公钥或者私钥
     * @param mode    指定是加密还是解密，值为Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE
     */
    private byte[] processData(byte[] srcData, Key key, int mode) {
        //用来保存处理结果
        byte[] resultBytes = null;
        try {
            //获取Cipher实例
            Cipher cipher = Cipher.getInstance(sTransform);
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(mode, key);
            //处理数据
            resultBytes = cipher.doFinal(srcData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return resultBytes;
    }

    /*
     分割线------------分段加密解密--------开始
     */

    //公钥分段加密
    private byte[] encryptByPublicKeyForSpilt(byte[] data, PublicKey publicKey) throws Exception {
        int dataLen = data.length;
        if (dataLen <= Default_BufferSize) {
            return processData(data, publicKey, Cipher.ENCRYPT_MODE);
        }
        List<Byte> allBytes = new ArrayList<Byte>(2048);
        int bufIndex = 0;
        int subDataLoop = 0;
        byte[] buf = new byte[Default_BufferSize];
        for (int i = 0; i < dataLen; i++) {
            buf[bufIndex] = data[i];
            if (++bufIndex == Default_BufferSize || i == dataLen - 1) {
                subDataLoop++;
                if (subDataLoop != 1) {
                    for (byte b : Default_Split) {
                        allBytes.add(b);
                    }
                }
                byte[] encryptBytes = processData(buf, publicKey, Cipher.ENCRYPT_MODE);
                for (byte b : encryptBytes) {
                    allBytes.add(b);
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(Default_BufferSize, dataLen - i - 1)];
                }
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b.byteValue();
            }
        }
        return bytes;
    }

    //私钥分段加密
    private byte[] encryptByPrivateKeyForSpilt(byte[] data, PrivateKey privateKey) throws Exception {
        int dataLen = data.length;
        if (dataLen <= Default_BufferSize) {
            return processData(data, privateKey, Cipher.ENCRYPT_MODE);
        }
        List<Byte> allBytes = new ArrayList<Byte>(2048);
        int bufIndex = 0;
        int subDataLoop = 0;
        byte[] buf = new byte[Default_BufferSize];
        for (int i = 0; i < dataLen; i++) {
            buf[bufIndex] = data[i];
            if (++bufIndex == Default_BufferSize || i == dataLen - 1) {
                subDataLoop++;
                if (subDataLoop != 1) {
                    for (byte b : Default_Split) {
                        allBytes.add(b);
                    }
                }
                byte[] encryptBytes = processData(buf, privateKey, Cipher.ENCRYPT_MODE);
                for (byte b : encryptBytes) {
                    allBytes.add(b);
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(Default_BufferSize, dataLen - i - 1)];
                }
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b.byteValue();
            }
        }
        return bytes;
    }

    //公钥分段解密
    private byte[] decryptByPublicKeyForSpilt(byte[] encrypted, PublicKey publicKey) throws Exception {
        int splitLen = Default_Split.length;
        if (splitLen <= 0) {
            return processData(encrypted, publicKey, Cipher.DECRYPT_MODE);
        }
        int dataLen = encrypted.length;
        List<Byte> allBytes = new ArrayList<Byte>(1024);
        int latestStartIndex = 0;
        for (int i = 0; i < dataLen; i++) {
            byte bt = encrypted[i];
            boolean isMatchSplit = false;
            if (i == dataLen - 1) {
                // 到data的最后了
                byte[] part = new byte[dataLen - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = processData(part, publicKey, Cipher.DECRYPT_MODE);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            } else if (bt == Default_Split[0]) {
                // 这个是以split[0]开头
                if (splitLen > 1) {
                    if (i + splitLen < dataLen) {
                        // 没有超出data的范围
                        for (int j = 1; j < splitLen; j++) {
                            if (Default_Split[j] != encrypted[i + j]) {
                                break;
                            }
                            if (j == splitLen - 1) {
                                // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                isMatchSplit = true;
                            }
                        }
                    }
                } else {
                    // split只有一位，则已经匹配了
                    isMatchSplit = true;
                }
            }
            if (isMatchSplit) {
                byte[] part = new byte[i - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = processData(part, publicKey, Cipher.DECRYPT_MODE);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b.byteValue();
            }
        }
        return bytes;
    }

    //私钥分段解密
    private byte[] decryptByPrivateKeyForSpilt(byte[] encrypted, PrivateKey privateKey) throws Exception {
        int splitLen = Default_Split.length;
        if (splitLen <= 0) {
            return processData(encrypted, privateKey, Cipher.DECRYPT_MODE);
        }
        int dataLen = encrypted.length;
        List<Byte> allBytes = new ArrayList<Byte>(1024);
        int latestStartIndex = 0;
        for (int i = 0; i < dataLen; i++) {
            byte bt = encrypted[i];
            boolean isMatchSplit = false;
            if (i == dataLen - 1) {
                // 到data的最后了
                byte[] part = new byte[dataLen - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = processData(part, privateKey, Cipher.DECRYPT_MODE);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            } else if (bt == Default_Split[0]) {
                // 这个是以split[0]开头
                if (splitLen > 1) {
                    if (i + splitLen < dataLen) {
                        // 没有超出data的范围
                        for (int j = 1; j < splitLen; j++) {
                            if (Default_Split[j] != encrypted[i + j]) {
                                break;
                            }
                            if (j == splitLen - 1) {
                                // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                isMatchSplit = true;
                            }
                        }
                    }
                } else {
                    // split只有一位，则已经匹配了
                    isMatchSplit = true;
                }
            }
            if (isMatchSplit) {
                byte[] part = new byte[i - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = processData(part, privateKey, Cipher.DECRYPT_MODE);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b.byteValue();
            }
        }
        return bytes;
    }

    /*
     分割线------------分段加密解密--------结束
     */

    //将字符串形式的公钥转换为公钥对象
    private PublicKey keyStrToPublicKey(String publicKeyStr) {
        PublicKey publicKey = null;
        byte[] keyBytes = Base64.decode(publicKeyStr, sBase64Mode);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    //将字符串形式的私钥，转换为私钥对象
    private PrivateKey keyStrToPrivate(String privateKeyStr) {
        PrivateKey privateKey = null;
        byte[] keyBytes = Base64.decode(privateKeyStr, sBase64Mode);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }


    /*
     分割线------------开放接口--------开始
     */

    //公钥加密数据，结果用Base64转码
    public String encryptDataByPublicKey(byte[] srcData, PublicKey publicKey) {
        if (srcData == null || publicKey == null)
            return null;
        //获得加密后的字节数组
        byte[] resultBytes = processData(srcData, publicKey, Cipher.ENCRYPT_MODE);
        if (resultBytes != null) {
            return Base64.encodeToString(resultBytes, sBase64Mode);
        } else {
            return null;
        }
    }

    public String encryptDataByPublicKey(byte[] srcData, String publicKeyString) {
        if (srcData == null || TextUtils.isEmpty(publicKeyString))
            return null;
        PublicKey publicKey = keyStrToPublicKey(publicKeyString);
        if (publicKey != null) {
            return encryptDataByPublicKey(srcData, publicKey);
        } else {
            return null;
        }
    }

    //私钥解密数据，转换为字符串，使用utf-8编码格式
    public String decryptedToStrByPrivate(String srcData, PrivateKey privateKey) {
        if (srcData == null || privateKey == null)
            return null;
        byte[] srcDataByte = Base64.decode(srcData, sBase64Mode);
        byte[] result = processData(srcDataByte, privateKey, Cipher.DECRYPT_MODE);
        if (result != null)
            return new String(result);
        else
            return null;
    }

    public String decryptedToStrByPrivate(String srcData, String privateKeyString) {
        if (srcData == null || TextUtils.isEmpty(privateKeyString))
            return null;
        PrivateKey privateKey = keyStrToPrivate(privateKeyString);
        if (privateKey != null) {
            byte[] srcDataByte = Base64.decode(srcData, sBase64Mode);
            byte[] result = processData(srcDataByte, privateKey, Cipher.DECRYPT_MODE);
            if (result != null)
                return new String(result);
            else
                return null;
        } else {
            return null;
        }
    }

    public String decryptedToStrByPrivate(String srcData, PrivateKey privateKey, String charset) {
        if (srcData == null || privateKey == null || TextUtils.isEmpty(charset))
            return null;
        try {
            byte[] bytes = Base64.decode(srcData, sBase64Mode);
            byte[] result = processData(bytes, privateKey, Cipher.DECRYPT_MODE);
            if (result != null)
                return new String(result, charset);
            else
                return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //私钥加密数据，结果用Base64转码
    public String encryptDataByPrivateKey(byte[] srcData, PrivateKey privateKey) {
        if (srcData == null || privateKey == null)
            return null;
        byte[] resultBytes = processData(srcData, privateKey, Cipher.ENCRYPT_MODE);
        if (resultBytes != null)
            return Base64.encodeToString(resultBytes, sBase64Mode);
        else
            return null;
    }

    public String encryptDataByPrivateKey(byte[] srcData, String privateKeyString) {
        if (srcData == null || TextUtils.isEmpty(privateKeyString))
            return null;
        PrivateKey privateKey = keyStrToPrivate(privateKeyString);
        if (privateKey != null) {
            return encryptDataByPrivateKey(srcData, privateKey);
        } else {
            return null;
        }
    }

    //使用公钥解密，转换为字符串，使用默认字符集utf-8
    public String decryptedToStrByPublicKey(String srcData, PublicKey publicKey) {
        if (srcData == null || publicKey == null)
            return null;
        byte[] bytes = Base64.decode(srcData, sBase64Mode);
        byte[] result = processData(bytes, publicKey, Cipher.DECRYPT_MODE);
        if (result != null)
            return new String(result);
        else
            return null;
    }

    public String decryptedToStrByPublicKey(String srcData, String publicKeyString) {
        if (srcData == null || TextUtils.isEmpty(publicKeyString))
            return null;
        PublicKey publicKey = keyStrToPublicKey(publicKeyString);
        if (publicKey != null) {
            return decryptedToStrByPublicKey(srcData, publicKey);
        } else {
            return null;
        }
    }

    //使用公钥解密，结果转换为字符串，使用指定字符集
    public String decryptedToStrByPublicKey(String srcData, PublicKey publicKey, String charset) {
        if (srcData == null || publicKey == null || TextUtils.isEmpty(charset))
            return null;
        try {
            byte[] bytes = Base64.decode(srcData, sBase64Mode);
            byte[] result = processData(bytes, publicKey, Cipher.DECRYPT_MODE);
            if (result != null)
                return new String(result, charset);
            else
                return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     分割线------------开放接口--------结束
     */

}
