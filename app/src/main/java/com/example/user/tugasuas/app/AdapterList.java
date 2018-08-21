package com.example.user.tugasuas.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.tugasuas.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AJISETYA.
 */
public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder>{

    Context context;
    ArrayList<HashMap<String, String>> list_data;

    public AdapterList(MainActivity mainActivity, ArrayList<HashMap<String, String>> list_data) {
        this.context = mainActivity;
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context)
                .load("http://sasmitoh.nitarahmawati.my.id/getdata.php" + list_data.get(position).get("image_path"))
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imgdata);
        holder.txtname.setText(list_data.get(position).get("image_name"));
        holder.txtnim.setText(list_data.get(position).get("nim"));
        holder.txtnohp.setText(list_data.get(position).get("nohp"));
        holder.txttgl_lahir.setText(list_data.get(position).get("tanggal"));
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtnim, txtnohp, txttgl_lahir, txtname;
        ImageView imgdata;

        public ViewHolder(View itemView) {
            super(itemView);

            txtnim =  (TextView) itemView.findViewById(R.id.txtnim);
            txtname = (TextView) itemView.findViewById(R.id.txtname);
            txtnohp =  (TextView) itemView.findViewById(R.id.txtnohp);
            txttgl_lahir =  (TextView) itemView.findViewById(R.id.txttgl_lahir);
            imgdata = (ImageView) itemView.findViewById(R.id.imgdata);
        }
    }
}

