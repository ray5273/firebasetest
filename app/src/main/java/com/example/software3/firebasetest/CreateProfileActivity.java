package com.example.software3.firebasetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfileActivity extends AppCompatActivity implements View.OnClickListener{
    EditText name,age,location,typeOfDisablility;
    Switch isDisable;
    Button submitBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        name = (EditText)findViewById(R.id.editName);
        age = (EditText)findViewById(R.id.editAge);
        location = (EditText)findViewById(R.id.editLocation);
        typeOfDisablility = (EditText)findViewById(R.id.TypeOfdisability);
        isDisable = (Switch)findViewById(R.id.isDisabled);
        submitBtn = (Button)findViewById(R.id.profile_submit_btn);
        submitBtn.setOnClickListener(this);


        isDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    typeOfDisablility.setVisibility(View.VISIBLE);
                }else{
                    typeOfDisablility.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    public void sendProfile(){

        boolean disable = false;

        if(isDisable.isChecked()){
            disable = true;
        }


        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        String userUid = mAuth.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String emailAddress = intent.getStringExtra("email");
        UserProfile profile = new UserProfile(emailAddress,name.getText().toString(),
                Integer.parseInt(age.getText().toString()),location.getText().toString(),disable,
                typeOfDisablility.getText().toString());
        mDatabase.child("users").child(userUid).setValue(profile);


        if(disable == false) {
            Intent nextActivity = new Intent(this, HelperMainActivity.class);
            startActivity(nextActivity);
            finish();
        }else{
            Intent nextActivity = new Intent(this,HelperMainActivity.class); //여기 장애인용으로 바꾸기
            startActivity(nextActivity);
            finish();
        }





    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.profile_submit_btn:
                sendProfile();
                break;
        }
    }
}
