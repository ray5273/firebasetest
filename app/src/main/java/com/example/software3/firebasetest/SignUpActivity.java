package com.example.software3.firebasetest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    ProgressBar progressBar;
    EditText editTextUsername, editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mAnaly;


    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextUsername = (EditText)findViewById(R.id.signup_email);
        editTextPassword = (EditText)findViewById(R.id.signup_password);
        progressBar = (ProgressBar)findViewById(R.id.signup_progressbar);
        tv = (TextView)findViewById(R.id.textView);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sign_up_btn).setOnClickListener(this);





    }


    private void registerUser(){
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if(username.isEmpty()){
                editTextUsername.setError("Email is required");
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains("@skku.edu")){
                editTextUsername.setError("Please enter a vaild email");
                editTextUsername.requestFocus();
                return;
            }


            if(password.isEmpty()){
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            if(password.length()<6){
                editTextPassword.setError("Minimum length of password should be 6");
                editTextPassword.requestFocus();
                return;

            }
            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                   progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"User Registered Successful",Toast.LENGTH_SHORT).show();
                    }else{
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){

                            Toast.makeText(getApplicationContext(),"You are already registered",Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });

        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "mail sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv.setText(
                getString(R.string.project_id,user.getEmail(),1));

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_up_btn:
                registerUser();
                break;

        }
    }
}
