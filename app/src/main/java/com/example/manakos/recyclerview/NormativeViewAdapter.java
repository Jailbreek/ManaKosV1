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
import com.example.manakos.activites.RatesAndNormatives.RateOrNormativeItemActivity;
import com.example.manakos.database.DatabaseRequests;
import com.example.manakos.models.Normative;
import com.example.manakos.models.Rate;

import java.util.ArrayList;

public class NormativeViewAdapter extends RecyclerView.Adapter<NormativeViewAdapter.MyViewHolder> {

    ArrayList<Normative> dataholder;
    private final Context context;
    private final Activity activity;
    private final DatabaseRequests databaseRequests;

    public NormativeViewAdapter(ArrayList<Normative> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        databaseRequests = new DatabaseRequests(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_normative, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.name.setText("Normatif: " + (dataholder.get(position).getName()));
        holder.value.setText("Harga: " + dataholder.get(position).getValue());
        holder.id = dataholder.get(position).getId();
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RateOrNormativeItemActivity.class);
                intent.putExtra("id_normative", dataholder.get(position).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, value;
        int id;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.displayNameNormative);
            value = (TextView) itemView.findViewById(R.id.displayValueNormative);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
