package wangqiang.website.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangq on 2017/5/15.
 */
public class EncryptTools {

    private static final String pubKey = "010001";
    private static final String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    private static final String nonce = "0CoJUm6Qyw8W8jud";


    public static Map<String, String> encryptedRequest(JSONObject jsonObject) {
        Map<String, String> returnMap = new HashMap<>();
        String str = jsonObject.toString();
        String secretKey = createSecretKey(16);

        try {
            String encText = aesEncrypt(aesEncrypt(str, nonce), secretKey);
            String encSecKey = rsaEncrypt(secretKey, pubKey, modulus);

            returnMap.put("params", encText);
            returnMap.put("encSecKey", encSecKey);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    //AES加密
    private static String aesEncrypt(String text, String secKey) throws Exception {
        byte[] raw = secKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // "算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    //字符填充
    private static String zfill(String result, int n) {
        if (result.length() >= n) {
            result = result.substring(result.length() - n, result.length());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = n; i > result.length(); i--) {
                stringBuilder.append("0");
            }
            stringBuilder.append(result);
            result = stringBuilder.toString();
        }
        return result;
    }

    private static String rsaEncrypt(String secKey, String pubKey, String modulus) {
        StringBuffer stringBuffer = new StringBuffer(secKey);
        //逆置私钥
        secKey = stringBuffer.reverse().toString();
        String hex = Hex.encodeHexString(secKey.getBytes());
        BigInteger bigInteger1 = new BigInteger(hex, 16);
        BigInteger bigInteger2 = new BigInteger(pubKey, 16);
        BigInteger bigInteger3 = new BigInteger(modulus, 16);
        BigInteger bigInteger4 = bigInteger1.pow(bigInteger2.intValue()).remainder(bigInteger3);
        String encSecKey= Hex.encodeHexString(bigInteger4.toByteArray());
        //字符填充
         encSecKey= EncryptTools.zfill(encSecKey, 256);
        return encSecKey;
    }

    /**
     * 随机生成的，16位
     *
     * @return
     */
    private static String createSecretKey(int size) {
        String keys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String key = "";
        for (int i = 0; i < size; i++) {
            int pos = (int)  Math.floor(Math.random() * keys.length());
            key = key + keys.charAt(pos);
        }
        return key;
    }
}
