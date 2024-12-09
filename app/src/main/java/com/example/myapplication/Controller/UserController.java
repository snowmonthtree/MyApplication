package com.example.myapplication.Controller;

import android.graphics.Bitmap;

import com.example.myapplication.data.User.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserController {
    @GET("api/user/login")
    Call<User> getUser(@Query("param1") String param1, @Query("param2") String param2);

    @POST("api/user/insert")
    Call<String> insertUser(@Body User user, @Query("Code") String Code);

    @POST("api/user/changePassword")
    Call<String> changePassword(@Query("Email") String Email, @Query("newPassword") String newPassword, @Query("Code") String Code);

    @GET("/api/user/getCode")
    Call<String> getCode(@Query("Email") String Email);

    @POST("/api/user/uploadAvatarImage")
    Call<String> uploadAvatarImage(@Query("userId") String userId, @Query("image") Bitmap bitmap);
}