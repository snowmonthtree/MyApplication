package com.example.myapplication.Controller;

import com.example.myapplication.data.LedResource.LedResource;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LedResourceController {
    // 获取最新的 8 条资源
    @GET("/api/led-resources/init")
    Call<List<LedResource>> getLatestLedResources();

    // 搜索 LED 资源
    @POST("/api/led-resources/search")
    Call<List<LedResource>> searchLedResources(
            @Query("userId") String userId,
            @Query("name") String name
    );
    @GET("api/led-resources/{imageName}")
    Call<ResponseBody> getImage(@Path("imageName") String imageName);
    @GET("api/led-resources/order-by-playback-volume")
    Call< List<LedResource>> orderByPlaybackVolume();
    @GET("api/led-resources/order-by-likes")
    Call<List<LedResource>> orderByLikes();
    @GET("api/led-resources/getresource/{resourceId}")
    Call<LedResource> getResourceById(@Path("resourceId") String resourceId);
}
