package com.example.lagrange_support.firebasetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;

public class ActivityAccount extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDb;
    private ImageView imageProfile;
    private TextView textName;
    private Button btnLogOut;
    private Button btnUsersList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textName =findViewById(R.id.txtViewUserName);
        imageProfile = findViewById(R.id.imgView_account);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(Intent.createChooser(intent,"select a picture for your profile"),
                            CAMERA_REQUEST_CODE);

                }
            }
        });

        btnLogOut = findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                if(mAuth.getCurrentUser()!=null){
                    mAuth.signOut();
                }
            }
        });

        btnUsersList=findViewById(R.id.btnUsersList);
        btnUsersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityAccount.this, ActivityUsers.class);
                startActivity(intent);
            }
        });

        mProgress = new ProgressDialog(ActivityAccount.this);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    mStorage = FirebaseStorage.getInstance().getReference();
                    mDb = FirebaseDatabase.getInstance().getReference().child("users");
                    mDb.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            textName.setText(dataSnapshot.child("name").getValue().toString());
                            String imageUrl = dataSnapshot.child("image").getValue().toString();
                            if(!imageUrl.equals("default") && !TextUtils.isEmpty(imageUrl))
                                Picasso.with(ActivityAccount.this).
                                        load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imageProfile);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("user info changes","valueEventListener cancelled");
                            Toast.makeText(ActivityAccount.this,"Error load image line 104 ", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    startActivity(new Intent(ActivityAccount.this,ActivityEmailPwLogin.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == CAMERA_REQUEST_CODE) && (resultCode == RESULT_OK)){

            if(mAuth.getCurrentUser()== null)
                return;
            mProgress.setMessage("Uploading image");
            mProgress.show();
            final Uri uri = data.getData();

            if(uri == null) {
                mProgress.dismiss();
                return;
            }

            if(mStorage==null){
                mStorage=FirebaseStorage.getInstance().getReference();

            }
            if(mDb==null){
                mDb = FirebaseDatabase.getInstance().getReference().child("users");

            }

            final StorageReference filepath = mStorage.child("files").child("Photos").child(getRandomString());
            final DatabaseReference currentUserDb = mDb.child(mAuth.getCurrentUser().getUid());
            currentUserDb.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String image = dataSnapshot.getValue().toString();

                    if(!image.equals("default") && !TextUtils.isEmpty(image)){
                       // Toast.makeText(ActivityAccount.this, "file url is :"+image+":   :"+
                         //       image.equals("default")+":    :"+TextUtils.isEmpty(image), Toast.LENGTH_LONG).show();
                        FirebaseStorage.getInstance().getReferenceFromUrl(image).delete().addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Toast.makeText(ActivityAccount.this, "file deleted", Toast.LENGTH_SHORT).show();
                                        Log.d("bbbb", "onSuccess: deleted file");
                                    }
                                }
                        ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Uh-oh, an error occurred!
                                Log.d("bbbb", "onFailure: did not delete file");
                            }
                        });
                        //Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                      /*  task.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ActivityAccount.this,"Deleted image succesfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(ActivityAccount.this,"Deleted image failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/
                    }

                    currentUserDb.child("image").removeEventListener(this);
                    filepath.putFile(uri).addOnSuccessListener(ActivityAccount.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgress.dismiss();
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(ActivityAccount.this,"photo uploaded into Firebase storage",Toast.LENGTH_SHORT).show();
                            Picasso.with(ActivityAccount.this).load(downloadUri).fit().centerCrop().into(imageProfile);
                            DatabaseReference currentUserDb = mDb.child(mAuth.getCurrentUser().getUid());
                            currentUserDb.child("image").setValue(downloadUri.toString());
                            Toast.makeText(ActivityAccount.this,"photo updated in FIrebase realtime DB",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(ActivityAccount.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(ActivityAccount.this,"error to upload photo"+e.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println("error ====="+e.getMessage());
                            Log.v("TAG vv","errorrrrr"+e.toString());
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private String getRandomString() {
        SecureRandom random = new SecureRandom();

        return new BigInteger(130, random).toString(32);
    }
}
