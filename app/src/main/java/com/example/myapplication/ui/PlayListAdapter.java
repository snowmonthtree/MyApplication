package com.example.myapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ListDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.PlayList.PlayList;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {

    private List<PlayList> localList;
    private Context context;

    // 构造函数初始化数据
    public PlayListAdapter(List<PlayList> localList,Context context) {
        this.localList = localList;
        this.context=context;
    }

    @NonNull
    @Override
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载 item 布局
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item, parent, false);
        return new PlayListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListViewHolder holder, int position) {
        PlayList playList = localList.get(position);
        // 为视图绑定数据
        holder.titleTextView.setText(playList.getPlaylistName());
        holder.createTimeTextView.setText("创建时间:"+playList.getCreateTime().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent=new Intent(context, ListDetailActivity.class);
                intent.putExtra("listId",playList.getPlaylistId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localList == null ? 0 : localList.size();
    }
    public void updateData(List<PlayList> newData) {
        localList.clear();
        localList.addAll(newData);
        notifyDataSetChanged();
    }

    // ViewHolder 内部类，用于持有视图组件
    public static class PlayListViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView createTimeTextView;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            // 初始化视图组件
            titleTextView = itemView.findViewById(R.id.titleTextView);
            createTimeTextView = itemView.findViewById(R.id.create_time);
        }
    }
}
