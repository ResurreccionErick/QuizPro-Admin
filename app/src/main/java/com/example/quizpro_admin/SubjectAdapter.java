package com.example.quizpro_admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<SubjectModel> subjList;
    private Dialog loadingDialog,editDialog;

    public SubjectAdapter(List<SubjectModel> subjList) {
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

        String title = subjList.get(position).getName(); //getting the name of subj in fire base.

        viewHolder.setData(title,position,this); //the "this" is the method of this adapter class(SubjectAdapter)
    }

    @Override
    public int getItemCount() {
        return subjList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView subjName;
        private ImageView btnDelete;
        private EditText txtEditSubjDialog;
        private Button btnEditSubjDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subjName = itemView.findViewById(R.id.tvSubjName); //palettes from add_subject_dialog
            btnDelete = itemView.findViewById(R.id.btnSubjDelete);

            loadingDialog = new Dialog(itemView.getContext()); //loading dialog
            loadingDialog.setContentView(R.layout.loading_progress_bar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext()); //loading dialog
            editDialog.setContentView(R.layout.edit_subject_bar);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            txtEditSubjDialog = editDialog.findViewById(R.id.txtEditSubjNameDialog); //palettes from edit_subject_bar
            btnEditSubjDialog = editDialog.findViewById(R.id.btnEditSubjDialog);

        }

        public void setData(String title,int position,SubjectAdapter adapter) {
            subjName.setText(title);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    txtEditSubjDialog.setText(subjList.get(position).getName()); //passing the subj name in the EditText of edit_subject_bar
                    editDialog.show();

                    return false;
                }
            });

            btnEditSubjDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(txtEditSubjDialog.getText().toString().isEmpty()){
                        txtEditSubjDialog.setError("Enter Subject Name");
                        txtEditSubjDialog.requestFocus();
                        return;
                    }
                    updateSubject(txtEditSubjDialog.getText().toString(),position,itemView.getContext(),adapter);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Subject")
                            .setMessage("Do you want to delete this subject?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSubject(position,itemView.getContext(), adapter);
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
    }


    private void deleteSubject(final int id, Context context,SubjectAdapter adapter) {
        loadingDialog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        int index = 1;
        Map<String,Object> catDoc = new ArrayMap<>();
        for(int i = 0; i < subjList.size(); i++){
            if(i != id){
                catDoc.put("CAT" + String.valueOf(index) + "_ID",subjList.get(i).getId());
                catDoc.put("CAT" + String.valueOf(index) + "_NAME",subjList.get(i).getName());
                index++;
            }
        }

        catDoc.put("COUNT", index-1);

        firestore.collection("QUIZ").document("Categories").set(catDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Subject Deleted", Toast.LENGTH_SHORT).show();

                        SubjectActivity.catList.remove(id); //remove in catList from subjectActivity

                        adapter.notifyDataSetChanged();

                        loadingDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    loadingDialog.dismiss();

                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void updateSubject(String subjNewName,int position,Context context,SubjectAdapter adapter) {
        editDialog.dismiss();

        loadingDialog.show();

        Map<String,Object> subjData = new ArrayMap<>();
        subjData.put("NAME", subjNewName); //change the name of document in firebase

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("QUIZ").document(subjList.get(position).getId())
        .update(subjData)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Map<String,Object> subjDoc = new ArrayMap<>();
                subjDoc.put("CAT" + String.valueOf(position + 1) + "_NAME",subjNewName); //putting the data into arrayMap

                firestore.collection("QUIZ").document("Categories") //updating the data in firestore
                        .update(subjDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        SubjectActivity.catList.get(position).setName(subjNewName); //update also in subject activity catList

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
}
