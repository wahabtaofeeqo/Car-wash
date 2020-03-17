package com.example.carwash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwash.helper.Motor;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Motor> motors;

    private Context context;

    public CarAdapter(List<Motor> motors, Context context) {
        this.motors = motors;
        this.context = context;
    }

    @NonNull
    @Override
    public CarAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_layout, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        final Motor motor = motors.get(position);
        holder.imgCar.setImageResource(motor.getImage());
        holder.txtCarName.setText(motor.getName());
    }

    @Override
    public int getItemCount() {
        return motors.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView imgCar;
        public TextView txtCarName;
        public View viewG;

        public CarViewHolder(View view) {
            super(view);

            viewG = view;
            imgCar = (AppCompatImageView) view.findViewById(R.id.imgCar);
            txtCarName = (TextView) view.findViewById(R.id.txtCarName);
        }
    }
}
