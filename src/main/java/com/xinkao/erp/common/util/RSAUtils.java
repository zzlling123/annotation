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
	    
	    private static final String RSA_KEY_ALGORITHM = "RSA";

	    
	    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	    
	    private static final int KEY_SIZE = 1024;
	    
	    public static final String PRIVATE_KEY  = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANJSY5fJCZhhKdlpynRu28BkI68I0+3OLECnt4mbOYAbiaDL9WCtj+zUEMuH2pM0chFZd6lS3Ayt/WRi6X8DjmoT/ajEht56rjbfxgNZDsyh05VdH3K0QexPSOkIW+hh9hdhmeehZGssgHKVB5fOrFssPHEHEYKzgCOhEqHlLuc1AgMBAAECgYEAqTB9zWx7u4juEWd45ZEIVgw4aGXBllt0Xc6NZrTn3JZKcH+iNNNqJCm0GQaAXkqiODKwgBWXzttoK4kmLHa/6D7rXouWN8PGYXj7DHUNzyOe3IgmzYanowp/A8gu99mJQJzyhZGQ+Uo9dZXAgUDin6HAVLaxF3yWD8/yTKWN4UECQQD8Q72r7qdAfzdLMMSQl50VxRmbdhQYbo3D9FmwUw6W1gy2jhJyPXMi0JZKdKaqhxMZIT3zy4jYqw8/0zF2xc5/AkEA1W+n24Ef3ucbPgyiOu+XGwW0DNpJ9F8D3ZkEKPBgjOMojM7oqlehRwgy52hU+HaL4Toq9ghL1SwxBQPxSWCYSwJAGQUO9tKAvCDh9w8rL7wZ1GLsG0Mm0xWD8f92NcrHE6a/NAv7QGFf3gAaJ+BR92/WMRPe9SMmu3ab2JS1vzX3OQJAdN70/T8RYo8N3cYxNzBmf4d59ee5wzQb+8WD/57QX5UraR8LS+s8Bpc4uHnqvTq8kZG2YI5eZ9YQ6XwlLVbVTQJAKOSXNT+XEPWaol1YdWZDvr2m/ChbX2uwz52s8577Tey96O4Z6S/YA7V6Fr7hZEzkNF+K0LNUd79EOB6m2eQq5w==";



	    
	    private static Map<String, String> initKey() throws Exception {
	        KeyPairGenerator keygen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
	        SecureRandom secrand = new SecureRandom();
	        
	        secrand.setSeed("initSeed".getBytes());
	        
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

	    
	    public static String encodeBase64String(byte[] key) {
	        return Base64.encodeBase64String(key);
	    }

	    
	    public static byte[] decodeBase64(String key) {
	        return Base64.decodeBase64(key);
	    }

	    
	    public static String encryptByPubKey(String data, String publicKey) throws Exception {
	        byte[] pubKey = RSAUtils.decodeBase64(publicKey);
	        byte[] enSign = encryptByPubKey(data.getBytes(), pubKey);
	        return Base64.encodeBase64String(enSign);
	    }

	    
	    public static byte[] encryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    }

	    
	    public static String encryptByPriKey(String data, String privateKey) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(privateKey);
	        byte[] enSign = encryptByPriKey(data.getBytes(), priKey);
	        return Base64.encodeBase64String(enSign);
	    }

	    
	    public static byte[] encryptByPriKey(byte[] data, byte[] priKey) throws Exception {
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	        return cipher.doFinal(data);
	    }

	    
	    public static byte[] decryptByPubKey(byte[] data, byte[] pubKey) throws Exception {
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    }

	    
	    public static String decryptByPubKey(String data, String publicKey) throws Exception {
	        byte[] pubKey = RSAUtils.decodeBase64(publicKey);
	        byte[] design = decryptByPubKey(Base64.decodeBase64(data), pubKey);
	        return new String(design);
	    }

	    
	    public static byte[] decryptByPriKey(byte[] data, byte[] priKey) throws Exception {
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        return cipher.doFinal(data);
	    }

	    
	    public static String decryptByPriKey(String data, String privateKey) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(privateKey);
	        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
	        return new String(design);
	    }
	    
	    public static String myDecryptByPriKey(String data) throws Exception {
	        byte[] priKey = RSAUtils.decodeBase64(PRIVATE_KEY);
	        byte[] design = decryptByPriKey(Base64.decodeBase64(data), priKey);
	        return new String(design);
	    }

	    
	    public static String sign(byte[] data, byte[] priKey) throws Exception {

	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);

	        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

	        signature.initSign(privateKey);

	        signature.update(data);
	        return Base64.encodeBase64String(signature.sign());
	    }

	    
	    public boolean verify(byte[] data, byte[] sign, byte[] pubKey) throws Exception {

	        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);

	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);

	        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

	        signature.initVerify(publicKey);

	        signature.update(data);

	        return signature.verify(sign);
	    }

	    public static void main(String[] args) {
	        try {
	            logger.info(RSAUtils.myDecryptByPriKey("palI8Guhj2FcRgKJL/v/+a4r86XLkOKwYzrMcni6niEhaKP8Tbh3eHpGnpn4AZr0PVuxiCFlQ2dHDtIVmYIoMJPyneiT6dll8bL8QyhFCdnTEo3mSrZagJpqHrv3iHfcTo/+6oD8OD3FLniOdRCSjXRMCjEcUomZjNkjNZondZE="));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
