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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewRetrofit> {
    List<TripsResult> tripsresults ;
    private final RecyclerListner recyclerListner;

    Context mcontext;
    public RecyclerViewAdapter(Context context, List<TripsResult> tripsresult, RecyclerListner recyclerListner) {
        this.mcontext = context;
        this.tripsresults = tripsresult;
        this.recyclerListner = recyclerListner;
    }

    @NonNull
    @Override
    public RecyclerViewRetrofit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.trip,parent,false);
        return new RecyclerViewRetrofit(view,recyclerListner);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.RecyclerViewRetrofit holder, int position) {
        holder.departure.setText(tripsresults.get(position).getDeparture());
        holder.destination.setText(tripsresults.get(position).getDestination());
    }

    @Override
    public int getItemCount() {
        return tripsresults.size();
    }

    public static class RecyclerViewRetrofit extends RecyclerView.ViewHolder {
        TextView departure,destination,condition,time;

        @SuppressLint("CutPasteId")
        public RecyclerViewRetrofit(@NonNull View itemView , RecyclerListner recyclerListner) {
            super(itemView);
            departure = itemView.findViewById(R.id.departureEditText);
            destination = itemView.findViewById(R.id.arrivalEditText);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerListner != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerListner.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}