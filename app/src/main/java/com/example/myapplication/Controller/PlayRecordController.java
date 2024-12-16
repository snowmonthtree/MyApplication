package com.example.myapplication.Controller;
import com.example.myapplication.PlayRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlayRecordController {

    // 获取指定用户的播放记录
    @GET("/play-records/show/{userId}")
    Call<List<PlayRecord>> showPlayRecordByUserId(@Path("userId") String userId);

    // 添加播放记录
    @POST("/play-records/add/{userId}/{resourceId}")
    Call<Void> addPlayRecord(@Path("userId") String userId, @Path("resourceId") String resourceId);

}