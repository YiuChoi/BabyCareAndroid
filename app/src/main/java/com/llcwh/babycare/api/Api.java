package com.llcwh.babycare.api;

import com.llcwh.babycare.model.LoginResponse;
import com.llcwh.babycare.model.RegisterResponse;
import com.llcwh.babycare.model.User;

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
    Observable<LoginResponse> login(@Body User user);

    @POST("/api/v1/get_info")
    @Headers("Content-Type: application/json")
    Observable<ResponseBody> getInfo();

    @POST("/api/v1/register")
    @Headers("Content-Type: application/json")
    Observable<RegisterResponse> register(@Body User  user);
}
