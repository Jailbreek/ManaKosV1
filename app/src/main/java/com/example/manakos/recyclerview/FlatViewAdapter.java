package com.example.manakos.recyclerview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manakos.R;
import com.example.manakos.activites.Flat.FlatItemActivity;
import com.example.manakos.models.Flat;

import java.util.ArrayList;

public class FlatViewAdapter extends RecyclerView.Adapter<FlatViewAdapter.MyViewHolder>{
    ArrayList<Flat> dataholder;
    private final Context context;
    private final Activity activity;

    public FlatViewAdapter(ArrayList<Flat> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_flat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.number.setText("Nomor unit: " + dataholder.get(position).getFlat_number());
        holder.personal_account.setText("Token Listrik: " + dataholder.get(position).getPersonal_account());
        holder.id = dataholder.get(position).getId();
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FlatItemActivity.class);
                intent.putExtra("id",dataholder.get(position).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView number, personal_account;
        int id;
        LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            number = (TextView)itemView.findViewById(R.id.displayNumber);
            personal_account = (TextView)itemView.findViewById(R.id.displayPersonalAccount);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
