package com.example.myapplication.Controller;
import com.example.myapplication.PlayList;
import com.example.myapplication.data.LedResource.LedResource;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

// 定义接口用于与后端进行交互
public interface LedListController {

    // 创建播放列表
    @POST("/ledlist/create-playlist")
    Call<String> createPlaylist(
            @Query("userId") String userId,
            @Query("playlistName") String playlistName
    );

    // 删除播放列表
    @POST("/ledlist/delete-playlist")
    Call<String> deletePlaylist(
            @Query("userId") String userId,
            @Query("playlistId") String playlistId
    );

    // 获取播放列表
    @GET("/ledlist/get-playlists")
    Call<List<PlayList>> getPlaylists(
            @Query("userId") String userId
    );

    // 向播放列表中添加资源
    @POST("/ledlist/add-resource")
    Call<String> addResourceToPlaylist(
            @Query("userId") String userId,
            @Query("playlistId") String playlistId,
            @Query("resourceId") String resourceId
    );

    // 从播放列表中删除最后一个资源
    @POST("/ledlist/remove-last-resource")
    Call<String> removeLastResourceFromPlaylist(
            @Query("userId") String userId,
            @Query("playlistId") String playlistId
    );

    // 获取播放列表中的所有资源
    @GET("/ledlist/get-playlist-resources")
    Call<List<LedResource>> getPlaylistResources(
            @Query("userId") String userId,
            @Query("playlistId") String playlistId
    );
}
