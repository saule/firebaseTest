package com.example.lagrange_support.firebasetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ActivityRegister extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private Button mRegistrationBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;
    private TextView txtLogin_actRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameField=findViewById(R.id.etxt_name);
        mEmailField=findViewById(R.id.etxt_email);
        mPasswordField=findViewById(R.id.etxt_password);
        mAuth=FirebaseAuth.getInstance();
        mRegistrationBtn=findViewById(R.id.btn_register);
        txtLogin_actRegister = findViewById(R.id.txtLogin_activityRegister);

        txtLogin_actRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityRegister.this,ActivityEmailPwLogin.class);
                startActivity(intent);
            }
        });
        mProgress=new ProgressDialog(this);
        mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                   // Intent intent = new Intent(ActivityRegister.this, ActivityEmailPwLogin.class);
                    //startActivity(intent);
                    mAuth.signOut();
                    finish();
                }
            }
        };

    }


    private void startRegister() {
        final String name = mNameField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String pw = mPasswordField.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pw)){
            mProgress.setMessage("Registering, please wait ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, pw).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if(task.isSuccessful()){
                                String user_id = mAuth.getCurrentUser().getUid();
                                Toast.makeText(ActivityRegister.this, user_id, Toast.LENGTH_SHORT).show();

                              //  HashMap<String,String> dataMap = new HashMap<String, String>();

                            //    dataMap.put("name","saule");
                               // dataMap.put("email", "emailstr");
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference currentUserDB = mDatabase.child("users").child(user_id);
                               // DatabaseReference mDb= FirebaseDatabase.getInstance().getReference().child("users");
                             //   DatabaseReference currentUserDB = mDb.child(mAuth.getCurrentUser().getUid());
                               currentUserDB.child("name").setValue(name).addOnCompleteListener(ActivityRegister.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ActivityRegister.this,"success",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(ActivityRegister.this,"error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                               currentUserDB.child("image").setValue("default");

                            }
                            else {
                                Log.w("authorization", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(ActivityRegister.this, "smthing wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
