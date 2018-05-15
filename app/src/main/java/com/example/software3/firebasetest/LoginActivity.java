package com.example.software3.firebasetest;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Helper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFirestore DB;
    private FirebaseAuth mAuth;
    EditText editTextEmail,editTextPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);
        findViewById(R.id.to_sign_up_btn).setOnClickListener(this);
        findViewById(R.id.sign_in_btn).setOnClickListener(this);
        editTextEmail = (EditText)findViewById(R.id.signin_email);
        editTextPassword = (EditText)findViewById(R.id.signin_password);
        progressBar = (ProgressBar)findViewById(R.id.signin_progressbar);



        if(mAuth.getCurrentUser()!=null){
            DB = FirebaseFirestore.getInstance();

            String userUid = mAuth.getUid();
            DocumentReference docRef = DB.collection("users").document(userUid);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document!=null && document.exists()){
                            Log.d("tag complete", "data: "+document.getData());
                            if(document.get("isDisable").equals(true)) {
                                Intent intent = new Intent(LoginActivity.this, DisabledMainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(LoginActivity.this, HelperMainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            //여기서 장애인 메뉴와 비장애인 메뉴를 나눠서 들어가게 해주면 된다.
                        }
                    }
                }
            });

        }

    }


    private void userLogin(){

        final String username = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        mAuth = FirebaseAuth.getInstance();



        Log.e("email:  ",username);
        Log.e("Password:  ",password);
        if(username.isEmpty()){
            editTextEmail.setError("Email is required");
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches() && username.contains("@skku.edu")){
            editTextEmail.setError("Please enter a vaild email");
            editTextEmail.requestFocus();
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


        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser user = mAuth.getCurrentUser();
                final String email=null;

                if(task.isSuccessful() && mAuth.getCurrentUser().isEmailVerified()){
                    progressBar.setVisibility(View.GONE);
                    Log.e("user 이름:",mAuth.getCurrentUser().getEmail());



                    DatabaseReference FDB = FirebaseDatabase.getInstance().getReference();
                    DB = FirebaseFirestore.getInstance();
                   final String userUid = mAuth.getUid();


                    DocumentReference docRef = DB.collection("users").document(userUid);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document!=null && document.exists()){
                                    Log.d("tag complete", "data: "+document.getData());
                                    if(document.get("isDisable").equals(true)) {
                                        Intent intent = new Intent(LoginActivity.this, DisabledMainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(LoginActivity.this, HelperMainActivity.class);
                                        startActivity(intent);
                                    }

                                    //여기서 장애인 메뉴와 비장애인 메뉴를 나눠서 들어가게 해주면 된다.
                                }else{
                                    Log.d("tag failed", "no such data");
                                    Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("email",username);
                                    startActivity(intent);
                                }

                            }
                        }
                    })
                    ;/*
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                UserProfile profile = documentSnapshot.toObject(UserProfile.class);

                                Log.e("get Email address: ", profile.getEmailAddress());

                        }
                    });
*/


              //      finish();
                }else {
                    progressBar.setVisibility(View.GONE);
                    if (user != null && !user.isEmailVerified()){
                        Toast.makeText(getApplicationContext(), "이메일 등록을 먼저해주세요", Toast.LENGTH_SHORT).show();


                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "인증메일을 전송하였습니다 이메일을 확인 해 주세요",
                                    Toast.LENGTH_LONG);
                        }
                    });
                }
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.to_sign_up_btn:
            startActivity(new Intent(this,SignUpActivity.class));
            break;

            case R.id.sign_in_btn:
            userLogin();
            break;
        }
    }
}

