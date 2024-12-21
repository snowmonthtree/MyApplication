package com.example.myapplication.Controller;

import com.example.myapplication.Audit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;

public interface AuditController {

    // 获取所有审计记录（按审计时间降序）
    @GET("/api/audit/init")
    Call<List<Audit>> getAllAudits();

    // 根据 auditId 获取单个审计记录
    @GET("/api/audit/{auditId}")
    Call<Audit> getAudit(@Path("auditId") String auditId);

    // 上传审计信息
    @POST("/api/audit/user-audit")
    Call<String> uploadAudit(@Query("userId") String userId,
                             @Query("resourceId") String resourceId,
                             @Query("auditName") String auditName);

    // 删除审计信息
    @POST("/api/audit/delete-audit")
    Call<String> deleteAudit(@Query("resourceId") String resourceId);
}