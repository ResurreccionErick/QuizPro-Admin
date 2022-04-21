package com.example.quizpro_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnFiles, btnStudent, btnQuiz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFiles = findViewById(R.id.btnFiles);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnStudent = findViewById(R.id.btnStudent);

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SubjectActivity.class));
            }
        });

    }
}