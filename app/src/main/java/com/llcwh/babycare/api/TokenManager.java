package com.llcwh.babycare.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.llcwh.babycare.Const;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/8/31 0031.
 */
public class TokenManager {
    private static String token = null;

    public static String getToken() {
        return token;
    }

    public static boolean hasToken() {
        return !TextUtils.isEmpty(token);
    }

    public static void clearToken() {
        token = null;
    }

    public static String refreshToken() {
        try {
            LlcService.getApi().login(new JSONObject(new Gson().toJson(Const.sUser)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ResponseBody loginResponse) {

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }
}
