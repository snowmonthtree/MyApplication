package com.example.myapplication;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    private List<Uri> localList;

    public LocalAdapter(List<Uri> localList) {
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
        Uri item = localList.get(position);
        holder.imageView.setImageURI(item);
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.localImageView);
        }
    }
}