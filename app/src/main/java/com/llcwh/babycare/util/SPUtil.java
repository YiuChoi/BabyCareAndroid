package com.llcwh.babycare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import com.llcwh.babycare.BabyApplication;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class SPUtil {

    public static void saveToken(String token) {
        SharedPreferences sharedPreferences = BabyApplication.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_APPEND);
        sharedPreferences.edit().putString(CommonUtil.getTokenKey(BabyApplication.getContext()), token).apply();
    }

    public static String getToken() {
        SharedPreferences sharedPreferences = BabyApplication.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_APPEND);
        return sharedPreferences.getString(CommonUtil.getTokenKey(BabyApplication.getContext()), null);
    }

    public static String getUser() {
        SharedPreferences sharedPreferences = BabyApplication.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_APPEND);
        String s = sharedPreferences.getString(CommonUtil.getUserKey(BabyApplication.getContext()), null);
        if (!TextUtils.isEmpty(s))
            return AesUtils.decryptString(s);
        else
            return null;
    }

    public static void saveUser(String user) {
        SharedPreferences sharedPreferences = BabyApplication.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_APPEND);
        sharedPreferences.edit().putString(CommonUtil.getUserKey(BabyApplication.getContext()), AesUtils.encryptString(user)).apply();
    }
}
