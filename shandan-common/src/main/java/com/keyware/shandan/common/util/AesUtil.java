package com.keyware.shandan.common.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

/**
 * AES加、解密算法工具类
 */
public class AesUtil {
    /**
     * 加密算法AES
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * key的长度，Wrong key size: must be equal to 128, 192 or 256
     * 传入时需要16、24、36
     */
    private static final Integer KEY_LENGTH = 16 * 8;

    /**
     * 算法名称/加密模式/数据填充方式
     * 默认：AES/ECB/PKCS5Padding
     */
    private static final String ALGORITHMS = "AES/ECB/PKCS5Padding";

    /**
     * 后端AES的key，由静态代码块赋值
     */
    public static String key;

    /**
     * 不能在代码中创建
     * JceSecurity.getVerificationResult 会将其put进 private static final Map<Provider,Object>中，导致内存缓便被耗尽
     */
    private static final BouncyCastleProvider PROVIDER = new BouncyCastleProvider();

    static {
        key = getKey();
    }

    /**
     * 获取key
     */
    public static String getKey() {
        StringBuilder uid = new StringBuilder();
        //产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < KEY_LENGTH / 8; i++) {
            //产生0-2的3位随机数
            int type = rd.nextInt(3);
            switch (type) {
                case 0:
                    //0-9的随机数
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    //ASCII在65-90之间为大写,获取大写随机
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                case 2:
                    //ASCII在97-122之间为小写，获取小写随机
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }

    /**
     * 加密
     *
     * @param content    加密的字符串
     * @param encryptKey key值
     */
    public static String encrypt(String content, String encryptKey) throws Exception {
        //设置Cipher对象
        Cipher cipher = Cipher.getInstance(ALGORITHMS, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), KEY_ALGORITHM));

        //调用doFinal
        // 转base64
        return Base64.encodeBase64String(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));

    }

    /**
     * 解密
     *
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        //base64格式的key字符串转byte
        byte[] decodeBase64 = Base64.decodeBase64(encryptStr);

        //设置Cipher对象
        Cipher cipher = Cipher.getInstance(ALGORITHMS,PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), KEY_ALGORITHM));

        //调用doFinal解密
        return new String(cipher.doFinal(decodeBase64));
    }

    /**
     * 从请求中拿到加密数据进行解密并返回
     * @param request 请求
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decrypt(HttpServletRequest request) throws Exception {
        //AES加密后的数据
        String data = request.getParameter("data");
        if(StringUtils.isBlank(data)){
            return null;
        }
        //后端RSA公钥加密后的AES的key
        String aesKey = request.getParameter("aesKey");
        //前端公钥
        String publicKey = request.getParameter("publicKey");

        //后端私钥解密的到AES的key
        byte[] plaintext = RsaUtil.decryptByPrivateKey(org.apache.commons.codec.binary.Base64.decodeBase64(aesKey), RsaUtil.getPrivateKey());
        aesKey = new String(plaintext);

        //AES解密得到明文data数据
        return decrypt(data, aesKey);
    }
}
