package cn.xylink.mting.utils;

import org.apaches.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptionUtil {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA";

    /**
     * String to hold name of the encryption padding.
     */
    public static final String PADDING = "RSA/NONE/NoPadding";

    /**
     * String to hold name of the security provider.
     */
    public static final String PROVIDER = "BC";

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws NoSuchProviderException
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(android.util.Base64.decode(publicKey, android.util.Base64.DEFAULT));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws NoSuchProviderException
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @param key  :The public key
     * @return Encrypted text
     * @throws Exception
     */
    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider  
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);

            // encrypt the plain text using the public key  
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @param key  :The private key
     * @return plain text
     * @throws Exception
     */
    public static String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider  
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);

            // decrypt the text using the private key  
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    //MD5加密，32位
    public static String MD5(String str) {
        return str == null ? null : MD5(str.getBytes());
    }

    public static String MD5(byte[] bytes) {
        String result = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(bytes);
            result = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}