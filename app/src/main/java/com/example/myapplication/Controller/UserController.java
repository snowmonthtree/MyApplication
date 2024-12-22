package com.example.myapplication.Controller;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.Resource;
import com.example.myapplication.data.User.User;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserController {
    @GET("api/user/login")
    Call<User> getUser(@Query("param1") String param1, @Query("param2") String param2);

    @POST("api/user/insert")
    Call<String> insertUser(@Body User user, @Query("Code") String Code);

    @POST("api/user/changePassword")
    Call<String> changePassword(@Query("email") String Email, @Query("newPassword") String newPassword, @Query("Code") String Code);

    @GET("/api/user/getCode")
    Call<String> getCode(@Query("email") String Email);

    @Multipart
    @PATCH("/api/user/uploadFile")
    Call<String> imageUpload(@Part("user") RequestBody user, @Part MultipartBody.Part fileUpload);
    @GET("/api/user/avatar/{userId}")
    Call<ResponseBody> getAvatar(@Path("userId") String userId);
    @GET("api/user/get-all-user")
    Call<List<User>> getAllUser();
    @POST("api/user/delete")
    Call<String> deleteUser(@Query("userId") String userId);


}