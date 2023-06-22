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
import com.example.manakos.activites.Tenant.TenantItemActivity;
import com.example.manakos.models.Tenant;

import java.util.ArrayList;

public class TenantViewAdapter extends RecyclerView.Adapter<TenantViewAdapter.MyViewHolder> {

    ArrayList<Tenant> dataholder;
    private final Context context;
    private final Activity activity;

    public TenantViewAdapter(ArrayList<Tenant> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_tenant, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.full_name.setText("Nama Lengkap: " + dataholder.get(position).getFull_name());
        holder.phone_number.setText("Nomor Telepon: " + dataholder.get(position).getPhone_number());
        holder.id = dataholder.get(position).getId_tenant();
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TenantItemActivity.class);
                intent.putExtra("id", dataholder.get(position).getId_tenant());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView full_name, phone_number;
        int id;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            full_name = (TextView) itemView.findViewById(R.id.displayFullName);
            phone_number = (TextView) itemView.findViewById(R.id.displayPhoneNumber);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
