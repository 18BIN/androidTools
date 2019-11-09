package com.tools.security;

import android.text.TextUtils;
import android.util.SparseArray;

/**
 * Created by Administrator on 2019/4/26.
 * 类描述:加密解密工具类
 */

public class SecureUtil {

    /**
     * 因为要存放加密解密相关的key，所以设置为单例对象
     */
    private SparseArray<String> k = new SparseArray<>();

    //构造方法私有，防止外部实例化
    private SecureUtil() {
    }

    public static SecureUtil getInstance() {
        return SecureUtilClassHolder.secureUtil;
    }

    private static class SecureUtilClassHolder {
        private static final SecureUtil secureUtil = new SecureUtil();
    }

    public SparseArray<String> getK() {
        return k;
    }

    //用aes加密
    private String aesEncrypt(String srcData) {
        String encryptData = "";
        //先判断相关的key是否存在
        if (TextUtils.isEmpty(k.get(2, "")))
            return encryptData;
        //先把Aes的密钥用Rsa解密出来
        String k1 = RsaUtil.getInstance().decryptedToStrByPrivate(k.get(2), k.get(1));
        if (TextUtils.isEmpty(k1))
            return encryptData;
        //最后再把解密出来的密钥传进去进行加密，获得加密后字符串
        encryptData = AesUtil.getInstance().encrypt(k1, srcData);
        return encryptData;
    }

    //用aes解密
    private String aesDecrypt(String srcData) {
        String decryptData = "";
        //先判断相关的key是否存在
        if (TextUtils.isEmpty(k.get(2, "")))
            return decryptData;
        //先把Aes的密钥用Rsa解密出来
        String k1 = RsaUtil.getInstance().decryptedToStrByPrivate(k.get(2), k.get(1));
        if (TextUtils.isEmpty(k1))
            return decryptData;
        //最后再把解密出来的密钥传进去进行解密，获得原始字符串
        decryptData = AesUtil.getInstance().decrypt(k1, srcData);
        return decryptData;
    }

}
