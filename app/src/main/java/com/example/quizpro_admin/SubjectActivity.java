package com.example.quizpro_admin;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    private RecyclerView subRecyclerView;
    private Button btnAddNewSubj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        subRecyclerView = findViewById(R.id.subjRecyclerView);

        Toolbar toolbar = findViewById(R.id.subjToolbar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("Subjects");
        getSupportActionBar().setTitle(title);

        List<String> subjList = new ArrayList<>();
        subjList.add("CAT 1");
        subjList.add("CAT 2");
        subjList.add("CAT 3");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        subRecyclerView.setLayoutManager(layoutManager);

        SubjectAdapter adapter = new SubjectAdapter(subjList); //it has subjList in loading page activity
        subRecyclerView.setAdapter(adapter);
    }

}