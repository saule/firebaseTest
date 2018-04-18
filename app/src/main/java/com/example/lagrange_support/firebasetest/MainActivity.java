package com.example.lagrange_support.firebasetest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private Button mFirebase;
    private EditText fname, email;
    private TextView db_list_field;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mFirebase = (Button)findViewById(R.id.firebase_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fname = (EditText)findViewById(R.id.fname);
        email = (EditText)findViewById(R.id.email);
       // db_list_field=(EditText)findViewById(R.id.db_list);


        mFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 - create a child in a root object
                //2 - assign some value to the child object
                String fnameStr=fname.getText().toString().trim();
                String emailStr = email.getText().toString().trim();

                HashMap<String,String> dataMap = new HashMap<String, String>();

                dataMap.put("name",fnameStr);
                dataMap.put("email", emailStr);
                mDatabase.push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //mDatabase.push().setValue(fnameStr);
                //mDatabase.child("name").setValue(fnameStr);


            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
