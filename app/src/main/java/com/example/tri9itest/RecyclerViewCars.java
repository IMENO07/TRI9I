package com.example.tri9itest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewCars extends RecyclerView.Adapter<RecyclerViewCars.RecyclerViewRetrofit> {
    List<CarResult> carResults;

    private final RecyclerListner recyclerListner;
    private final recyclerListnerLong recyclerListnerLong;

    Context mContext;

    public RecyclerViewCars(Context context, List<CarResult> carResults, RecyclerListner recyclerListner, recyclerListnerLong recyclerListnerLong) {
        this.mContext = context;
        this.carResults = carResults;
        this.recyclerListner = recyclerListner;
        this.recyclerListnerLong = recyclerListnerLong;
    }

    @NonNull
    @Override
    public RecyclerViewRetrofit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_car, parent, false);
        return new RecyclerViewRetrofit(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRetrofit holder, int position) {
        CarResult carResult = carResults.get(position);
        holder.brand.setText(carResult.getBrand());
        holder.model.setText(carResult.getModel());
        holder.places.setText(String.valueOf(carResult.getPlaces()));
        holder.plaque.setText(String.valueOf(carResult.getPlaque()));
    }

    @Override
    public int getItemCount() {
        return carResults.size();
    }

    public class RecyclerViewRetrofit extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView brand, model, places, plaque;

        public RecyclerViewRetrofit(@NonNull View itemView) {
            super(itemView);
            brand = itemView.findViewById(R.id.brand);
            model = itemView.findViewById(R.id.model);
            places = itemView.findViewById(R.id.placestxt);
            plaque = itemView.findViewById(R.id.plaque);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerListner != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    recyclerListner.onItemClick(pos);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (recyclerListnerLong != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    recyclerListnerLong.onItemLongClick(position);
                    return true;
                }
            }
            return false;
        }
    }
}