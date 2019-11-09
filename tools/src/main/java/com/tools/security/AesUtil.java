package com.tools.security;

import android.text.TextUtils;
import android.util.Base64;

import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2019/4/25.
 * 类描述:Aes加密解密工具类
 * Aes(非对称加密)，适合加量大量数据(有一个动态密钥)
 */

public class AesUtil {

    private final static String HEX = "0123456789ABCDEF";

    //AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";

    private static final String AES = "AES";  //AES 加密
    private static final String SHA1PRNG = "SHA1PRNG";  //SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法

    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int sBase64Mode = Base64.DEFAULT;

    //构造方法私有，防止外部实例化
    private AesUtil() {
    }

    public static AesUtil getInstance() {
        return AesUtilClassHolder.aesUtil;
    }

    private static class AesUtilClassHolder {
        private static final AesUtil aesUtil = new AesUtil();
    }

    //生成随机数，可以当做动态的密钥，加密和解密的密钥必须一致，不然将不能解密
    public String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes_key = new byte[20];
            localSecureRandom.nextBytes(bytes_key);
            return toHex(bytes_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 对密钥进行处理(得到密钥的字节数组)
    private byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        //for Android
        SecureRandom sr = null;
        // 在4.2以上版本中，SecureRandom获取方式发生了改变
        int sdk_version = android.os.Build.VERSION.SDK_INT;
        if (sdk_version > 23) {  // Android  6.0 以上
            sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        } else if (sdk_version >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        // for Java
        // secureRandom = SecureRandom.getInstance(SHA1PRNG);
        sr.setSeed(seed);
        keyGenerator.init(128, sr); //256 bits or 128 bits,192bits
        //AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    //加密
    public String encrypt(String key, String srcData) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(srcData)) {
            return null;
        }
        try {
            byte[] result = encryptData(key, srcData.getBytes());
            //return Base64Encoder.encode(result);
            return Base64.encodeToString(result, sBase64Mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //加密
    public String encrypt(String key, byte[] srcData) {
        if (TextUtils.isEmpty(key) || srcData == null) {
            return null;
        }
        try {
            byte[] result = encryptData(key, srcData);
            //return Base64Encoder.encode(result);
            return Base64.encodeToString(result, sBase64Mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //加密数据
    private byte[] encryptData(String key, byte[] srcDataByte) throws Exception {
        byte[] keyBytes = getRawKey(key.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(srcDataByte);
    }

    //解密
    public String decrypt(String key, String srcData) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(srcData)) {
            return null;
        }
        try {
            //byte[] enc = Base64Decoder.decodeToBytes(srcData);
            byte[] enc = Base64.decode(srcData, sBase64Mode);
            byte[] result = decryptData(key, enc);
            if (result != null)
                return new String(result);
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解密
    public String decrypt(String key, byte[] srcData) {
        if (TextUtils.isEmpty(key) || srcData == null) {
            return null;
        }
        try {
            byte[] result = decryptData(key, srcData);
            if (result != null)
                return new String(result);
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解密数据
    private byte[] decryptData(String key, byte[] encrypted) throws Exception {
        byte[] raw = getRawKey(key.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }

    public static class CryptoProvider extends Provider {
        /**
         * Creates a Provider and puts parameters
         */
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG",
                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }

    //二进制转字符
    private String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte b : buf) {
            appendHex(result, b);
        }
        return result.toString();
    }

    private void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
