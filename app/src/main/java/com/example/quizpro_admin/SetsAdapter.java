package com.example.quizpro_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder> {
    private List<String> setID;

    public SetsAdapter(List<String> setID) {
        this.setID = setID;
    }

    @NonNull
    @Override
    public SetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subj_item_list,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetsAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return setID.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSetName;
        private ImageView imgDeleteSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSetName = itemView.findViewById(R.id.tvSubjName);
            imgDeleteSet = itemView.findViewById(R.id.btnSubjDelete);
        }

        public void setData(int position) {
            tvSetName.setText("SET " + String.valueOf(position + 1));
        }
    }
}
