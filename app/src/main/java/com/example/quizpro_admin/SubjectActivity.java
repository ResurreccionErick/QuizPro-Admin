package com.example.quizpro_admin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubjectActivity extends AppCompatActivity {

    private RecyclerView subRecyclerView;
    private Button btnAddNewSubj;
    public static List<SubjectModel> subjList = new ArrayList<>();
    public static int selected_subj_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog,addSubjDialog;
    private SubjectAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        btnAddNewSubj = findViewById(R.id.btnAddNewSubj);
        subRecyclerView = findViewById(R.id.subjRecyclerView);

        Toolbar toolbar = findViewById(R.id.subjToolbar);
        setSupportActionBar(toolbar);

        loadingDialog = new Dialog(SubjectActivity.this); //loading dialog
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSubjDialog = new Dialog(SubjectActivity.this);
        addSubjDialog.setContentView(R.layout.add_subject_dialog);
        addSubjDialog.setCancelable(true);
        addSubjDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText dialogSubjName = (EditText) addSubjDialog.findViewById(R.id.txtAddSubjName);
        Button btnAddSubjDialog = (Button) addSubjDialog.findViewById(R.id.btnAddSubjDialog);

        firestore = FirebaseFirestore.getInstance();

        btnAddNewSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSubjName.getText().clear();
                addSubjDialog.show();
            }
        });

        btnAddSubjDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogSubjName.getText().toString().isEmpty()){
                    dialogSubjName.setError("Please Enter Subject Name");
                    dialogSubjName.requestFocus();
                    return;
                }else{
                    addNewSubject(dialogSubjName.getText().toString());
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        subRecyclerView.setLayoutManager(layoutManager);

        loadData();

    }

    private void loadData()
    {
        loadingDialog.show();

        subjList.clear();

        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {
                        long count = (long)doc.get("COUNT");

                        for(int i=1; i <= count; i++)
                        {
                            String subjName = doc.getString("CAT" + String.valueOf(i) + "_NAME");
                            String subjID = doc.getString("CAT" + String.valueOf(i) + "_ID");

                            subjList.add(new SubjectModel(subjID,subjName,"0","1")); //going to subjectModel
                        }

                        adapter = new SubjectAdapter(subjList);
                        subRecyclerView.setAdapter(adapter);

                    }
                    else
                    {
                        Toast.makeText(SubjectActivity.this,"No Subject Document Exists!",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                else
                {

                    Toast.makeText(SubjectActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }
        });

    }

    private void addNewSubject(final String title)
    {
        addSubjDialog.dismiss();
        loadingDialog.show();

        final Map<String,Object> catData = new ArrayMap<>();
        catData.put("NAME",title); //subject name inserted
        catData.put("SETS",0); //sets starts 0
        catData.put("COUNTER","1");

        final String doc_id = firestore.collection("QUIZ").document().getId(); //create an id

        firestore.collection("QUIZ").document(doc_id)
                .set(catData)   //inserted the arrayMap catData into its ID in firestore
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("CAT" + String.valueOf(subjList.size() + 1) + "_NAME",title);
                        catDoc.put("CAT" + String.valueOf(subjList.size() + 1) + "_ID",doc_id);
                        catDoc.put("COUNT", subjList.size() + 1);

                        firestore.collection("QUIZ").document("Categories")
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(),"Category added successfully",Toast.LENGTH_SHORT).show();

                                        subjList.add(new SubjectModel(doc_id,title,"0","1"));

                                        adapter.notifyItemInserted(subjList.size());

                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SubjectActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubjectActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });


    }


}