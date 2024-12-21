package com.example.myapplication.Controller;
import com.example.myapplication.Feedback;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

// 定义前端接口类
public interface FeedBackController {

    // 添加用户反馈
    @POST("/feedback/add/{userId}")
    Call<String> addFeedback(@Path("userId") String userId, @Body String context);

    // 获取所有反馈
    @GET("/feedback/getAll")
    Call<List<Feedback>> getAllFeedback();
}
