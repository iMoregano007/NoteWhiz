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

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        confirmPasswordEditText = findViewById(R.id.confirm_password_editText);
        progressBar = findViewById(R.id.createAccount_progressBar);
        createAccountBtn = findViewById(R.id.createAccount_btn);
        loginBtnTextView = findViewById(R.id.login_textViewBtn);

        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v ->  startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));
    }

    void createAccount(){
//        create account function
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);

        if(!isValidated){
            return;
        }

        createAccountInFirebase(email, password);

    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            Utility.showToast(CreateAccountActivity.this, "Successfully created account, Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
                            finish();

                        } else {
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());



                        }
                    }
                });

    }

    void changeInProgress(boolean inProgress){
//        setting the progress bar
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){
//        validating the credentials for typo errors
         if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
             emailEditText.setError("Email is invalid");
             return false;
         }
         if(password.length()<6){
             passwordEditText.setError("Password length is invalid");
             return false;
         }
         if(!password.equals(confirmPassword)){
             confirmPasswordEditText.setError("Password does not matches");
             return false;
         }
         return true;
    }
}