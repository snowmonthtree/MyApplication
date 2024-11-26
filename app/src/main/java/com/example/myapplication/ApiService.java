package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/login")
    Call<User> getUser(@Query("param1") String param1, @Query("param2") String param2);
    @POST("api/insert")
    Call<String> insertUser(@Body User user);
    @POST("api/changePassword")
    Call<String> changePassword(@Query("Email")String Email,@Query("newPassword")String newPassword);
}