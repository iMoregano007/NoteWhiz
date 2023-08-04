package com.imoregano007.notewhiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginAccountBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        progressBar = findViewById(R.id.loginAccount_progressBar);
        loginAccountBtn = findViewById(R.id.loginAccount_btn);
        createAccountBtnTextView = findViewById(R.id.createAccount_textViewBtn);


        loginAccountBtn.setOnClickListener(v -> loginUser());
        createAccountBtnTextView.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated = validateData(email,password);

        if(!isValidated){
            return;
        }
        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email, String password) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        Utility.showToast(LoginActivity.this, "LoggedIn Successfully");
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    } else {
                        Utility.showToast(LoginActivity.this,"Email not verified, Please verify your email");

                    }

                }else {
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());

                }
            }
        });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password length is invalid");
            return false;
        }

        return true;
    }
}