package com.example.myapplication;

import com.example.myapplication.Controller.UserController;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.data.User.User;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // 用于图片加载，若使用 Glide 加载头像
import com.example.myapplication.data.ViewSharer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.UserViewHolder> {

    private List<User> list;

    private UserController userController;
    private Context context;
    private String userId;

    public ManageUserAdapter(List<User> list, UserController userController,Context context,String userId) {
        this.list = list;
        this.userController = userController;
        this.context =context;
        this.userId=userId;
    }



    // 创建新视图（视图持有者）
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用 LayoutInflater 创建布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manage_user_layout, parent, false);
        return new UserViewHolder(view);
    }

    // 绑定数据到视图
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = list.get(position);
        if (!user.getUserId().equals(userId)) {
            // 设置文本内容
            holder.nameTextView.setText(user.getName());
            holder.emailTextView.setText(user.getUserId());
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("删除该用户?")
                            .setMessage("该操作无法撤销")
                            .setNegativeButton("取消", (dialog, which) -> {

                            })
                            .setPositiveButton("确定", (dialog, which) -> {
                                Call<String> call = userController.deleteUser(user.getUserId());
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })

                            .show();
                }
            });
        }
        else {
            holder.nameTextView.setText(user.getName()+" (我) ");
            holder.emailTextView.setText(user.getUserId());
            holder.imageView.setVisibility(View.GONE);
        }

        // 可以根据需求在这里设置其他的用户数据
    }
    public void updateData(List<User> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }
    // 获取数据源的大小
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // 内部的 ViewHolder 类，用来缓存每个用户项的视图
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView emailTextView;
        ImageView imageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化视图组件
            nameTextView = itemView.findViewById(R.id.Username);
            emailTextView = itemView.findViewById(R.id.Userid);
            imageView = itemView.findViewById(R.id.deleteUser);
        }
    }
}
