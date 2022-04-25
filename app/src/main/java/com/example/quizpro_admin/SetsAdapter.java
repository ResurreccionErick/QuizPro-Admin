package com.example.quizpro_admin;

import static com.example.quizpro_admin.SubjectActivity.selected_subj_index;
import static com.example.quizpro_admin.SubjectActivity.subjList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder> {
    private List<String> setIDs;


    public SetsAdapter(List<String> setID) {
        this.setIDs = setID;
    }

    @NonNull
    @Override
    public SetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subj_item_list,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetsAdapter.ViewHolder holder, int position) {
        String setId = setIDs.get(position);
        holder.setData(position,setId,this);

    }

    @Override
    public int getItemCount() {
        return setIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Dialog loadingDialog;
        private TextView tvSetName;
        private ImageView imgDeleteSet;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            loadingDialog = new Dialog(itemView.getContext()); //loading dialog
            loadingDialog.setContentView(R.layout.loading_progress_bar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            tvSetName = itemView.findViewById(R.id.tvSubjName);
            imgDeleteSet = itemView.findViewById(R.id.btnSubjDelete);

        }

        public void setData(final int position,final String setID,SetsAdapter adapter) {

            tvSetName.setText("SET " + String.valueOf(position + 1));

            imgDeleteSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Set")
                            .setMessage("Do you want to delete this Set?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSet(position,setID,itemView.getContext(),adapter);
                                }
                            }).setNegativeButton("Cancel", null)
                            .setIcon(android.R.drawable.ic_dialog_alert).show();

                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.BLUE);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0); //cancel button has 50 margin to the right
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
                }

            });
        }


    private void deleteSet(int position,String setID,final Context context,SetsAdapter adapter) {
        loadingDialog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("QUIZ").document(subjList.get(selected_subj_index).getId())
        .collection(setID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = firestore.batch();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    batch.delete(doc.getReference());
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String,Object> subjDoc = new ArrayMap<>();
                        int index = 1;
                        for(int i = 0; i < setIDs.size(); i++){
                            if(i != position){
                                subjDoc.put("SET" + String.valueOf(index) + "_ID", setIDs.get(i)); //get id 1 by 1 then store it into subject category document
                                index++;
                            }
                        }

                        subjDoc.put("SETS",index-1);

                        firestore.collection("QUIZ").document(subjList.get(selected_subj_index).getId()).update(subjDoc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context.getApplicationContext(), "Set Deleted", Toast.LENGTH_SHORT).show();

                                SetsActivity.setsId.remove(position);

                                subjList.get(selected_subj_index).setNoOfSets(String.valueOf(SetsActivity.setsId.size()));
                                adapter.notifyDataSetChanged();

                                loadingDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
            }
        });


        }
    }
}