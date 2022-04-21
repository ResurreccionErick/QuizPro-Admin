package com.example.quizpro_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<String> subjList;

    public SubjectAdapter(List<String> subjList) {
        this.subjList = subjList;
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subj_item_list,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder viewHolder, int position) {
        String title = subjList.get(position);

        viewHolder.setData(title);
    }

    @Override
    public int getItemCount() {
        return subjList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subjName;
        private ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subjName = itemView.findViewById(R.id.tvSubjName); //palettes from subj_item_list
            btnDelete = itemView.findViewById(R.id.btnSubjDelete);
        }

        public void setData(String title) {
            subjName.setText(title);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
