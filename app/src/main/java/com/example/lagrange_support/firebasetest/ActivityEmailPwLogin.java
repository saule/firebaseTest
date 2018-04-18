package com.example.lagrange_support.firebasetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityEmailPwLogin extends AppCompatActivity {

    private EditText mEmailLoginText;
    private EditText mPwLoginText;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginemailpw);

        mProgress=new ProgressDialog(ActivityEmailPwLogin.this);
        mEmailLoginText=findViewById(R.id.etxt_login_email);
        mPwLoginText=findViewById(R.id.etxt_login_pw);
        mAuth=FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterBtn = findViewById(R.id.btn_register);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityEmailPwLogin.this,ActivityRegister.class);
                startActivity(intent);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Toast.makeText(ActivityEmailPwLogin.this,
                            "now you are logged in" + firebaseAuth.getCurrentUser().getUid(),
                            Toast.LENGTH_SHORT).show();
                   // mAuth.signOut();

                    Intent intent;
                    if(!getIntent().getBooleanExtra("goToActivityUsers",false))
                          intent =new Intent(ActivityEmailPwLogin.this,ActivityAccount.class);
                    else
                        intent =new Intent(ActivityEmailPwLogin.this,ActivityUsers.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void doLogin() {
        String email = mEmailLoginText.getText().toString().trim();
        String pw = mPwLoginText.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pw)){
            mProgress.setMessage("Loging, please wait ...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgress.dismiss();
                    if(task.isSuccessful()){
                        Toast.makeText(ActivityEmailPwLogin.this,
                                "Login successful",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ActivityEmailPwLogin.this,
                                "smthing wrong",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


