package com.example.carwash;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwash.fragment.ServicesFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceHolder> {

    private List<RequestData> list;
    private Context context;
    private ServicesFragment fragment;

    public static class ServiceHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView image;
        public TextView date;
        public TextView status;
        public MaterialButton confirm;
        public View v;

        public ServiceHolder(View view) {
            super(view);

            v = view;
            date = (TextView) view.findViewById(R.id.date);
            status = (TextView) view.findViewById(R.id.status);
            confirm = (MaterialButton) view.findViewById(R.id.confirm);
        }
    }


    public ServiceAdapter(Context context, List<RequestData> data, Fragment fragment) {
        list = data;
        this.context = context;
        this.fragment = (ServicesFragment) fragment;
    }

    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new ServiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        final RequestData data = list.get(position);

        if (position % 2 == 0)
            holder.v.setBackgroundColor(Color.rgb(255, 255, 255));
        else
            holder.v.setBackgroundColor(Color.TRANSPARENT);

        holder.date.setText(data.getCreated());
        if (data.getStatus() == 0) {
            holder.status.setText("Pending");

            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.confirm(data);
                }
            });
        }
        else {
            holder.status.setText("Completed");
            holder.confirm.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}