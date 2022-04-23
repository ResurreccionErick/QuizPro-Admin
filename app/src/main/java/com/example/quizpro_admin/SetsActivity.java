package com.example.quizpro_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private RecyclerView setsView;
    private Button btnAddNewSets;
    private SetsAdapter adapter;

    public static List<String> setsId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        setsView = findViewById(R.id.sets_subjRecyclerView);
        btnAddNewSets = findViewById(R.id.btnAddNewSets);

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

        setsId.add("A");
        setsId.add("B");
        setsId.add("C");

        adapter = new SetsAdapter(setsId);
        setsView.setAdapter(adapter);


    }
}