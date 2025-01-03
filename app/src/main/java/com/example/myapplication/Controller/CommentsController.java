package com.example.myapplication.Controller;

import com.example.myapplication.data.Comment.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentsController {
    @GET("/api/comments/find/resource/{resourceId}")
    Call<List<Comment>> getCommentsByResourceId(@Path("resourceId") String resourceId);

    @POST("/api/comments/add/resource/{resourceId}/{userId}")
    Call<String> addComment(
            @Path("resourceId") String resourceId,
            @Path("userId") String userId,
            @Body String commentContext
    );
    @GET("/api/comments/get-all")
    Call<List<Comment>> getAllComments();

    // 获取某个用户的评论
    @GET("/api/comments/get-user-comment")
    Call<List<Comment>> getUserComments(@Query("userId") String userId);
    @DELETE("/api/comments/delete/{commentId}")
    Call<String> deleteComment(@Path("commentId") String commentId);
}
