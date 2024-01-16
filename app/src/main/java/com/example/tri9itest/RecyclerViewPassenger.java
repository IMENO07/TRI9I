package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewPassenger extends RecyclerView.Adapter<RecyclerViewPassenger.RecyclerViewRetrofit> {
    List<Passenger> passengers;
    Context context;

    public RecyclerViewPassenger(Context context, List<Passenger> passengers) {
        this.context = context;
        this.passengers = passengers;
    }

    @NonNull
    @Override
    public RecyclerViewRetrofit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.passenger, parent, false);
        return new RecyclerViewRetrofit(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRetrofit holder, int position) {
        Passenger passenger = passengers.get(position);
        holder.namePassenger.setText(passenger.getNamePassenger());
        holder.phonePassenger.setText(passenger.getPhonePassenger());
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    public static class RecyclerViewRetrofit extends RecyclerView.ViewHolder {
        TextView namePassenger, phonePassenger;

        public RecyclerViewRetrofit(@NonNull View itemView) {
            super(itemView);
            namePassenger = itemView.findViewById(R.id.namePassenger);
            phonePassenger = itemView.findViewById(R.id.numberPassenger);
        }
    }
}