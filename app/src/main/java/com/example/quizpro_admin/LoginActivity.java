package com.example.quizpro_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmail, txtPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance(); //Initialization for firebase authentication

        loadingDialog = new Dialog(LoginActivity.this); //loading dialog for sets
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(true);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                String email = txtEmail.getText().toString().trim();
//                String password = txtPassword.getText().toString().trim();
//
//                if(email.isEmpty()) {
//                    txtEmail.setError("Please Enter Your Email Address");
//                    txtEmail.requestFocus();
//                    return;
//                }
//                 if(password.isEmpty()) {
//                     txtPassword.setError("Please Enter Your Password");
//                     txtPassword.requestFocus();
//                     return;
//                } if(password.length() < 6){
//                    txtPassword.setError("Please Enter 6 Digits Password");
//                    txtPassword.requestFocus();
//                    return;
//                } if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//                    txtEmail.setError("Please Enter Valid Email Address");
//                    txtEmail.requestFocus();
//                    return;
//                }else{
//                    firebaseLogin(); //calling the login method
//                }
            }
        });
    }

    private void firebaseLogin() { //method that check and login the admin
        mAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}