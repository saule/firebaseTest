package com.example.lagrange_support.firebasetest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

public class ActivityUsers  extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDb;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ArrayList<UserDB> entries = new ArrayList<>();
    private Button btnAccountPage;
    private Button btnAccountPage1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mRecyclerView=findViewById(R.id.mActAccountRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        mDb=FirebaseDatabase.getInstance().getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null){
                    Intent intent = new Intent(ActivityUsers.this,ActivityEmailPwLogin.class);
                    intent.putExtra("goToActivityAsers", true);
                    startActivity(intent);
                    finish();
                }
                else{
                    getUsers();
                }
            }
        };

        btnAccountPage=findViewById(R.id.btnAccountPage_act_users);
        btnAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityUsers.this, ActivityAccount.class);
                startActivity(intent);
            }
        });

      /*  btnAccountPage1=findViewById(R.id.btnAccountPage_user_row);
        btnAccountPage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityUsers.this, ActivityAccount.class);
                startActivity(intent);
            }
        });*/
    }

    private static class UserDB{
        public String name,image;

        public UserDB(String name,String image){
            this.image=image;
            this.name=name;
        }
    }

    private void getUsers() {
        mDb.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                Toast.makeText(ActivityUsers.this,"Total users = "+dataSnapshot.getChildrenCount(),Toast.LENGTH_LONG).show();
                entries.clear();
                while(items.hasNext()){
                    DataSnapshot item = items.next();
                    String name, image;
                    name=item.child("name").getValue().toString();
                    image=item.child("image").getValue().toString();
                    UserDB entry=new UserDB(name, image);
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println(entry.name);
                    entries.add(entry);

                }

                mRecyclerView.setAdapter(new RecUsersAdapter(ActivityUsers.this,entries));
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mDb.child("users").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class RecUsersAdapter extends RecyclerView.Adapter<RecUsersAdapter.RecViewHolder>{

        private Context context;
        private ArrayList<UserDB> entries;

        public RecUsersAdapter(Context context, ArrayList<UserDB> entries) {
            this.context=context;
            this.entries=entries;
        }


        @Override
        public RecUsersAdapter.RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.user_row,null);
            return new RecViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecUsersAdapter.RecViewHolder holder, int position) {
            UserDB user=entries.get(position);

            try {
                if (!user.image.equals("default")) {
                    Picasso.with(context).load(user.image).fit().centerCrop().into(holder.imageView);
                }

                holder.textView.setText(user.name);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }

        public class RecViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView textView;

            public RecViewHolder(View itemView) {
                super(itemView);

                imageView=itemView.findViewById(R.id.imageUser_user_row);
                textView=itemView.findViewById(R.id.txtUser_userRow);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
