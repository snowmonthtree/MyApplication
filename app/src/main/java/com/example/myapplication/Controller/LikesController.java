package com.example.myapplication.Controller;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LikesController {

    // 判断用户是否点赞
    @GET("/api/likes/userIfLike")
    Call<Boolean> userIfLike(@Query("userId") String userId, @Query("resourceId") String resourceId);

    // 点赞/取消点赞
    @POST("/api/likes/userLike")
    Call<String> likeResource(@Query("userId") String userId, @Query("resourceId") String resourceId);
    @GET("/api/likes/likesnum")
    Call<Integer> getLikesNum(@Query("resourceId") String resourceId);
    }
