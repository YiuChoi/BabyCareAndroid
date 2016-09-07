package com.llcwh.babycare.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.llcwh.babycare.BabyApplication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class CommonUtil {

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getTokenKey(Context context) {
        return md5(getImei(context) + "token");
    }

    public static String getUserKey(Context context) {
        return md5(getImei(context) + "user");
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static void logout(){
        SPUtil.saveToken(null);
        final Intent intent = BabyApplication.getContext().getPackageManager().getLaunchIntentForPackage(BabyApplication.getContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        BabyApplication.getContext().startActivity(intent);
    }
}
