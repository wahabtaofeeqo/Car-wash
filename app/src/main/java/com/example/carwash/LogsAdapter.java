package com.example.carwash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carwash.helper.ServiceRequest;

import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogHolder> {

    private List<ServiceRequest> requests;
    private Context context;

    public static class LogHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView image;
        public TextView date;
        public TextView address;

        public LogHolder(View view) {
            super(view);

            image = (AppCompatImageView) view.findViewById(R.id.image);
            date = (TextView) view.findViewById(R.id.date);
            address = (TextView) view.findViewById(R.id.address);
        }
    }

    public LogsAdapter(Context ctx, List<ServiceRequest> serviceRequests) {
        requests = serviceRequests;
        context = ctx;
    }

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_log, parent, false);

        return new LogHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        ServiceRequest request = requests.get(position);
        Glide.with(context).load(R.drawable.car).into(holder.image);
        holder.date.setText(request.getDate());
        holder.address.setText(request.getAddress());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
