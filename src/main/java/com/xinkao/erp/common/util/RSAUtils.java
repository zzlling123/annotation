package com.xinkao.erp.common.util;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtils {

	 private static Logger logger = LoggerFactory.getLogger(RSAUtils.class);
	    /**
	     * 数字签名，密钥算法
	     */
	    private static final String RSA_KEY_ALGORITHM = "RSA";

	    /**
	     * 数字签名签名/验证算法
	     */
	    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	    /**
	     * RSA密钥长度，RSA算法的默认密钥长度是1024密钥长度必须是64的倍数，在512到65536位之间
	     */
	    private static final int KEY_SIZE = 1024;
	    /**
	     * 私钥
	     */
	    public static final String PRIVATE_KEY  = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANJSY5fJCZhhKdlpynRu28BkI68I0+3OLECnt4mbOYAbiaDL9WCtj+zUEMuH2pM0chFZd6lS3Ayt/WRi6X8DjmoT/ajEht56rjbfxgNZDsyh05VdH3K0QexPSOkIW+hh9hdhmeehZGssgHKVB5fOrFssPHEHEYKzgCOhEqHlLuc1AgMBAAECgYEAqTB9zWx7u4juEWd45ZEIVgw4aGXBllt0Xc6NZrTn3JZKcH+iNNNqJCm0GQaAXkqiODKwgBWXzttoK4kmLHa/6D7rXouWN8PGYXj7DHUNzyOe3IgmzYanowp/A8gu99mJQJzyhZGQ+Uo9dZXAgUDin6HAVLaxF3yWD8/yTKWN4UECQQD8Q72r7qdAfzdLMMSQl50VxRmbdhQYbo3D9FmwUw6W1gy2jhJyPXMi0JZKdKaqhxMZIT3zy4jYqw8/0zF2xc5/AkEA1W+n24Ef3ucbPgyiOu+XGwW0DNpJ9F8D3ZkEKPBgjOMojM7oqlehRwgy52hU+HaL4Toq9ghL1SwxBQPxSWCYSwJAGQUO9tKAvCDh9w8rL7wZ1GLsG0Mm0xWD8f92NcrHE6a/NAv7QGFf3gAaJ+BR92/WMRPe9SMmu3ab2JS1vzX3OQJAdN70/T8RYo8N3cYxNzBmf4d59ee5wzQb+8WD/57QX5UraR8LS+s8Bpc4uHnqvTq8kZG2YI5eZ9YQ6XwlLVbVTQJAKOSXNT+XEPWaol1YdWZDvr2m/ChbX2uwz52s8577Tey96O4Z6S/YA7V6Fr7hZEzkNF+K0LNUd79EOB6m2eQq5w==";



	    /**
	     * 生成密钥对
	     */
	    private static Map<String, String> initKey() throws Exception {
	        KeyPairGenerator keygen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
	        SecureRandom secrand = new SecureRandom();
	        /**
	         * 初始化随机产生器
	         */
	        secrand.setSeed("initSeed".getBytes());
	        /**
	         * 初始化密钥生成器
	         */
	        keygen.initialize(KEY_SIZE, secrand);
	        KeyPair keys = keygen.genKeyPair();

	        byte[] pub_key = keys.getPublic().getEncoded();
	        String publicKeyString = Base64.encodeBase64String(pub_key);

	        byte[] pri_key = keys.getPrivate().getEncoded();
	        String privateKeyString = Base64.encodeBase64String(pri_key);

	        Map<String, String> keyPairMap = new HashMap<>();
	        keyPairMap.put("publicKeyString", publicKeyString);
	        keyPairMap.put("privateKeyString", privateKeyString);

	        return keyPairMap;
	    }

	    /**
	     * 密钥转成字符串
	     *
	     * @param key
	     * @return
	     */
	    public static String encodeBase64String(byte[] key) {
	        return Base64.encodeBase64String(key);
	    }

	    /**
	     * 密钥转成byte[]
	     *
	     * @param key
	     * @return
	     */
	    public static byte[] decodeBase64(String key) {
	        return Base64.decodeBase64(key);
	    }

	    /**
	     * 公钥加密
	     *
	     * @param data      加密前的字符串
	     * @param publicKey 公钥
	     * @return 加密后的字符串
	     * @throws Exception
	     */
	    public static String encryptByPubKey(String data, String publicKey) throws Exception {
	        byte[] pubKey = RSAUtils.decodeBase64(publicKey);
	        byte[] enSign = encryptByPubKey(data.getBytes(), pubKey);
	        return Base64.encodeBase64String(enSign);
	    }

	    /**
	     * 公钥加密
	     *
	     * @param data   待加密数据
	     * @param pubKey 公钥
	     * @return
	     * @throws Exception
	     */
	    public static byte[] encryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    }

	    /**
	     * 私钥加密
	     *
	     * @param data       加密前的字符串
	     * @param privateKey 私钥
	     * @return 加密后的字符串
	     * @throws Exception
	     */
	    public static String encryptByPriKey(String data, String privateKey) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(privateKey);
	        byte[] enSign = encryptByPriKey(data.getBytes(), priKey);
	        return Base64.encodeBase64String(enSign);
	    }

	    /**
	     * 私钥加密
	     *
	     * @param data   待加密的数据
	     * @param priKey 私钥
	     * @return 加密后的数据
	     * @throws Exception
	     */
	    public static byte[] encryptByPriKey(byte[] data, byte[] priKey) throws Exception {
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	        return cipher.doFinal(data);
	    }

	    /**
	     * 公钥解密
	     *
	     * @param data   待解密的数据
	     * @param pubKey 公钥
	     * @return 解密后的数据
	     * @throws Exception
	     */
	    public static byte[] decryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    }

	    /**
	     * 公钥解密
	     *
	     * @param data      解密前的字符串
	     * @param publicKey 公钥
	     * @return 解密后的字符串
	     * @throws Exception
	     */
	    public static String decryptByPubKey(String data, String publicKey) throws Exception {
	        byte[] pubKey = RSAUtils.decodeBase64(publicKey);
	        byte[] design = decryptByPubKey(Base64.decodeBase64(data), pubKey);
	        return new String(design);
	    }

	    /**
	     * 私钥解密
	     *
	     * @param data   待解密的数据
	     * @param priKey 私钥
	     * @return
	     * @throws Exception
	     */
	    public static byte[] decryptByPriKey(byte[] data, byte[] priKey) throws Exception {
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        return cipher.doFinal(data);
	    }

	    /**
	     * 私钥解密
	     *
	     * @param data       解密前的字符串
	     * @param privateKey 私钥
	     * @return 解密后的字符串
	     * @throws Exception
	     */
	    public static String decryptByPriKey(String data, String privateKey) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(privateKey);
	        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
	        return new String(design);
	    }
	    /**
	     * 私钥解密
	     *
	     * @param data       解密前的字符串
	     * @param privateKey 私钥
	     * @return 解密后的字符串
	     * @throws Exception
	     */
	    public static String myDecryptByPriKey(String data) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(PRIVATE_KEY);
	        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
	        return new String(design);
	    }

	    /**
	     * RSA签名
	     *
	     * @param data   待签名数据
	     * @param priKey 私钥
	     * @return 签名
	     * @throws Exception
	     */
	    public static String sign(byte[] data, byte[] priKey) throws Exception {
	        // 取得私钥
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        // 生成私钥
	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        // 实例化Signature
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        // 初始化Signature
	        signature.initSign(privateKey);
	        // 更新
	        signature.update(data);
	        return Base64.encodeBase64String(signature.sign());
	    }

	    /**
	     * RSA校验数字签名
	     *
	     * @param data   待校验数据
	     * @param sign   数字签名
	     * @param pubKey 公钥
	     * @return boolean 校验成功返回true，失败返回false
	     */
	    public boolean verify(byte[] data, byte[] sign, byte[] pubKey) throws Exception {
	        // 实例化密钥工厂
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        // 初始化公钥
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
	        // 产生公钥
	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	        // 实例化Signature
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        // 初始化Signature
	        signature.initVerify(publicKey);
	        // 更新
	        signature.update(data);
	        // 验证
	        return signature.verify(sign);
	    }

	    public static void main(String[] args) {
	        try {
//	            Map<String, String> keyMap = initKey();
//	            String publicKeyString = keyMap.get("publicKeyString");
//	            String privateKeyString = keyMap.get("privateKeyString");
//	            logger.info("公钥:" + publicKeyString);
//	            logger.info("私钥:" + privateKeyString);
//
//	            // 待加密数据
//	            String data = "admin123";
//	            // 公钥加密
//	            String encrypt = RSAUtils.encryptByPubKey(data, publicKeyString);
//	            // 私钥解密
//	            String decrypt = RSAUtils.decryptByPriKey(encrypt, privateKeyString);
//
//	            logger.info("加密前:" + data);
//	            logger.info("加密后:" + encrypt);
//	            logger.info("解密后:" + decrypt);
	            
	            logger.info(RSAUtils.myDecryptByPriKey("palI8Guhj2FcRgKJL/v/+a4r86XLkOKwYzrMcni6niEhaKP8Tbh3eHpGnpn4AZr0PVuxiCFlQ2dHDtIVmYIoMJPyneiT6dll8bL8QyhFCdnTEo3mSrZagJpqHrv3iHfcTo/+6oD8OD3FLniOdRCSjXRMCjEcUomZjNkjNZondZE="));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
