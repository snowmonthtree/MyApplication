package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.CommentsController;
import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.data.LedResource.LedResource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCommentAdapter extends RecyclerView.Adapter<ManageCommentAdapter.CommentViewHolder> {

    private List<Comment> list;  // 评论数据列表
    private Context context;  // 上下文，用于显示 Toast 或弹出对话框
    private CommentsController commentsController;  // 用于处理评论相关的后台操作（如删除评论等）

    // 构造函数，初始化数据列表、上下文和控制器
    public ManageCommentAdapter(List<Comment> list,  CommentsController commentsController,Context context) {
        this.list = list;
        this.context = context;
        this.commentsController = commentsController;
    }

    // 创建新的视图持有者
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取视图布局
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        return new CommentViewHolder(view);
    }

    // 绑定数据到视图
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = list.get(position);


            // 设置评论信息
            holder.userNameTextView.setText(comment.getUserId());
            holder.commentTextView.setText(comment.getCommentContext());
            holder.fromLed.setText(comment.getResourceId());

            // 设置删除评论的点击事件
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("删除资源?")
                            .setMessage("该操作无法撤销")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", (dialog, which) -> {
                                // 调用控制器删除资源
                                Call<String> call = commentsController.deleteComment(comment.getCommentId());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()) {

                                            Toast.makeText(context, "资源删除成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            })
                            .show();
                }
            });

            // 可以根据需求设置其他操作，比如修改评论等

    }

    // 返回数据集的大小
    @Override
    public int getItemCount() {
        return list.size();
    }
    public void updateData(List<Comment> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }
    // 自定义视图持有者类
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;  // 用户名
        TextView commentTextView;   // 评论内容
        ImageView deleteImageView; // 删除评论的按钮
        TextView fromLed;

        public CommentViewHolder(View itemView) {
            super(itemView);
            // 初始化视图
            userNameTextView = itemView.findViewById(R.id.Username);
            commentTextView = itemView.findViewById(R.id.comment);
            fromLed =itemView.findViewById(R.id.comment_from);
            deleteImageView = itemView.findViewById(R.id.deleteComment);
        }
    }
}
