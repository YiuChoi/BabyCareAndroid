package com.llcwh.babycare.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    public static final String AES_KEY = "72GdiNwoWphKeT5d";

    public static void enCode(String filePath) {
        // AES算法要求密鈅128位(16字节)
        try {
            SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            // 加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            File file = new File(filePath);
            byte[] source = new byte[(int) file.length()];
            FileInputStream is = new FileInputStream(file);
            is.read(source, 0, (int) file.length());
            is.close();
            // 加密
            byte[] enc = cipher.doFinal(source);
            // 输出到文件
            FileOutputStream os = new FileOutputStream(new File(filePath));
            os.write(enc, 0, enc.length);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptString(String s) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(s.getBytes());
            return parseByte2HexStr(encrypted);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String decryptString(String s) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(parseHexStr2Byte(s));
            return new String(decrypted);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
