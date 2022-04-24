package com.example.quizpro_admin;

import static com.example.quizpro_admin.SubjectActivity.selected_subj_index;
import static com.example.quizpro_admin.SubjectActivity.subjList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Sets;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private RecyclerView setsView;
    private Button btnAddNewSets;
    private SetsAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    public static List<String> setsId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);
        setsView = findViewById(R.id.sets_subjRecyclerView);
        btnAddNewSets = findViewById(R.id.btnAddNewSets);

        firestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(SetsActivity.this); //loading dialog
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        btnAddNewSets.setText("ADD NEW SET");

        btnAddNewSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(layoutManager);

        loadSets();
    }

    private void loadSets() {
        //fetch sets from firebase firestore
        setsId.clear();
        loadingDialog.show();

        firestore.collection("QUIZ").document(subjList.get(selected_subj_index).getId()) //get the id of what clicked
        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 long noOfSets = (long) documentSnapshot.get("SETS"); //get the SETS from firebase

                for(int i = 1; i <= noOfSets; i++){
                    setsId.add(documentSnapshot.getString("SET")+String.valueOf(i)+"_ID");
                }

                subjList.get(selected_subj_index).setSetCounter(documentSnapshot.getString("COUNTER")); //set value from the firestore
                subjList.get(selected_subj_index).setNoOfSets(String.valueOf(noOfSets));

                adapter = new SetsAdapter(setsId);
                setsView.setAdapter(adapter);

                loadingDialog.dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();



            }
        });
    }
}