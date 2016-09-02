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
        synchronized (monitor) {
            if (api == null) {
                api = new Retrofit.Builder()
                        .baseUrl("http://10.10.11.158:5000")
                        .client(client)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(Api.class);
            }
            return api;
        }
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
            return chain.proceed(modifiedRequest);
        }
    }
}
