package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.AuditController;
import com.example.myapplication.Controller.LedResourceController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAuditAdapter extends RecyclerView.Adapter<ManageAuditAdapter.AuditViewHolder> {

    private List<Audit> list; // 用于存储审核数据的列表
    private AuditController auditController; // 后端接口控制器
    private Context context; // 上下文，用于获取系统资源或启动其他活动
    private LedResourceController ledResourceController; // 资源控制器
    private String userId;

    // 构造方法
    public ManageAuditAdapter(List<Audit> list, AuditController auditController, Context context, LedResourceController ledResourceController,String userId) {
        this.list = list;
        this.auditController = auditController;
        this.context = context;
        this.ledResourceController = ledResourceController;
        this.userId=userId;
    }

    // 创建 ViewHolder
    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载每个项的布局
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_manage_caution_resouce_layout, parent, false);
        return new AuditViewHolder(itemView);
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, int position) {
        Audit audit = list.get(position);

        // 显示审核信息
        holder.auditNameTextView.setText("举报人Id:"+audit.getUser().getUserId());
        holder.auditTimeTextView.setText("资源名:"+audit.getResource().getName()+ "\n 举报原因:"+ audit.getAuditName());
        fetchImage(audit.getAuditUrl(),holder.imageView);


        // 设置删除审核的点击事件
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("删除审核")
                    .setMessage("确定要删除这个审核记录吗?")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 调用删除接口
                        Call<String> call = auditController.deleteAudit(audit.getResource().getResourceId());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "删除成功"+response.body(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(context, "请求失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .show();
        });
        holder.deleteButton1.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("删除资源")
                    .setMessage("确定要删除这个资源吗?该操作无法撤销")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 调用删除接口
                        Call<String> call = ledResourceController.deleteResource(userId,audit.getResource().getResourceId());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "删除成功"+response.body(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(context, "请求失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .show();
        });
    }

    // 返回列表的总项数
    @Override
    public int getItemCount() {
        return list.size();
    }

    // 定义 ViewHolder 类
    public static class AuditViewHolder extends RecyclerView.ViewHolder {
        TextView auditNameTextView;
        TextView auditTimeTextView;
        ImageButton deleteButton; // 删除按钮
        ImageButton deleteButton1;
        ImageView imageView;

        public AuditViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化 ViewHolder 的视图组件
            auditNameTextView = itemView.findViewById(R.id.whoAudit);
            auditTimeTextView = itemView.findViewById(R.id.resource_name);
            deleteButton = itemView.findViewById(R.id.deleteAudit);
            deleteButton1 = itemView.findViewById(R.id.deleteLed);
            imageView=itemView.findViewById(R.id.iv_img);
        }
    }

    // 更新适配器的数据列表
    public void updateList(List<Audit> newList) {
        list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }
    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();  // 返回字节数组
    }
    // 判断是否是 GIF 格式
    private boolean isGif(byte[] imageData) {
        if (imageData.length >= 4) {
            return imageData[0] == 'G' && imageData[1] == 'I' && imageData[2] == 'F';
        }
        return false;
    }
    // 将ResponseBody转换为字节数组
    private byte[] convertResponseBodyToBytes(ResponseBody responseBody) {
        byte[] data = null;
        InputStream inputStream = responseBody.byteStream();
        try {
            data = readBytesFromStream(inputStream);  // 读取字节流到字节数组
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();  // 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    private void fetchImage(String viewWebUrl, ImageView imageView) {
        // 假设 ledResourceController 是 Retrofit 接口，调用 getImage 方法来获取图片
        ledResourceController.getImage(viewWebUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ResponseBody responseBody = response.body();
                        byte[] imageData = convertResponseBodyToBytes(responseBody);  // 获取字节数组

                        // 读取字节流并判断是否为 GIF 格式
                        if (isGif(imageData)) {
                            // 如果是 GIF 动图
                            Glide.with(context)
                                    .asGif()
                                    .load(imageData)  // 加载字节数组
                                    .placeholder(R.drawable.ic_caution_refresh)
                                    .error(R.drawable.ic_doodle_back)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);  // 显示 GIF 动图
                        } else {
                            // 如果是静态图片（如 PNG, JPEG）
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            if (bitmap != null) {
                                Glide.with(context)
                                        .load(imageData)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.ic_caution_refresh)
                                        .error(R.drawable.ic_doodle_back)
                                        .into(imageView);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Image Fetch", "Failed to process response", e);
                    }
                } else {
                    Log.e("Image Fetch", "onResponse: Image fetch failed");
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("Image Fetch", "onFailure: Image fetch failed", t);
            }
        });
    }
}

