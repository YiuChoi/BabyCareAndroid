package com.llcwh.babycare.api;

import android.text.TextUtils;

import com.llcwh.babycare.util.SPUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.llcwh.babycare.util.CommonUtil.logout;

/**
 * Created by 蔡小木 on 2016/8/31 0031.
 */
public class LlcService {

    private LlcService() {
    }

    private static final Object monitor = new Object();

    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new TokenInterceptor()).build();

    private static Api api = null;

    public static Api getApi() {
        if (api == null) {
            synchronized (monitor) {
                api = new Retrofit.Builder()
                        //.baseUrl("http://120.26.76.211:5000")
                        .baseUrl("http://192.168.2.210:5000")
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(Api.class);
            }
        }
        return api;
    }

    private static class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request modifiedRequest = request;
            String token = SPUtil.getToken();
            if (!TextUtils.isEmpty(token)) {
                modifiedRequest = request.newBuilder()
                        .addHeader("Authorization", "JWT " + token)
                        .build();
            }
            Response originalResponse = chain.proceed(modifiedRequest);
            if (originalResponse.code() == 401 && !TextUtils.isEmpty(token)) {
                logout();
            }
            return originalResponse;
        }
    }
}
