package com.llcwh.babycare.api;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/8/31 0031.
 */
public interface Api {

    @POST("/auth")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> login(@Body String json);
}
