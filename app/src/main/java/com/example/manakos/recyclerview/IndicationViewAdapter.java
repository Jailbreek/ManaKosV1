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
import com.example.manakos.activites.Counter.CounterItemActivity;
import com.example.manakos.activites.Indication.IndicationItemActivity;
import com.example.manakos.database.DatabaseRequests;
import com.example.manakos.models.Counter;
import com.example.manakos.models.Indication;

import java.util.ArrayList;

public class IndicationViewAdapter extends RecyclerView.Adapter<IndicationViewAdapter.MyViewHolder> {

    ArrayList<Indication> dataholder;
    private final Context context;
    private final Activity activity;
    private final DatabaseRequests databaseRequests;

    public IndicationViewAdapter(ArrayList<Indication> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        databaseRequests = new DatabaseRequests(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_indication, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.flat_number.setText("Nomor Kos: " + databaseRequests.selectFlatNumberFromId(
                databaseRequests.selectIdFlatFromCounter(dataholder.get(position).getId_counter())));
        holder.service.setText(
                "Counter: " + databaseRequests.selectTypeFromCounter(dataholder.get(position).getId_counter()));
        holder.period.setText("Periode: " + dataholder.get(position).getPeriod());
        holder.value.setText("Harga: " + dataholder.get(position).getValue());
        holder.id = dataholder.get(position).getId();
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IndicationItemActivity.class);
                intent.putExtra("id", dataholder.get(position).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView flat_number, service, period, value;
        int id;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            flat_number = (TextView) itemView.findViewById(R.id.displayNumberFlat);
            service = (TextView) itemView.findViewById(R.id.displayService);
            period = itemView.findViewById(R.id.displayPeriod);
            value = itemView.findViewById(R.id.displayValue);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
