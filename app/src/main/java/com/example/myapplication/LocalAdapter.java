package com.example.myapplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    private List<String> localList;

    public LocalAdapter(List<String> localList) {
        this.localList = localList;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LocalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        String item = localList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}