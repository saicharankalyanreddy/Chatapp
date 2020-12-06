package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Groupmembers extends AppCompatActivity {


    private View friendsview;

    String groupname;
    RecyclerView friendlist;


    FloatingActionButton fab;

    DatabaseReference friendref;

    FirebaseAuth mauth;
    String cuserid;

    DatabaseReference userref;

    String userimager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmembers);


        friendlist = findViewById(R.id.addmembers);


        friendlist.setHasFixedSize(true);
        friendlist.setLayoutManager(new LinearLayoutManager(this));



        mauth = FirebaseAuth.getInstance();

        groupname = getIntent().getStringExtra("group_name");

        fab = findViewById(R.id.fab);
        cuserid = mauth.getUid();
        friendref = FirebaseDatabase.getInstance().getReference().child("Friends").child(cuserid);
        friendref.keepSynced(true);
        userref = FirebaseDatabase.getInstance().getReference().child("users");
        userref.keepSynced(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Groupmembers.this,Groupactivity.class));
                finish();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        }

        Toast.makeText(Groupmembers.this,"Click on the Names to select or unselect  the members",Toast.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendref, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, addgroupmembersholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, addgroupmembersholder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final Groupmembers.addgroupmembersholder holder, final int position, @NonNull Friends model) {
                holder.date.setText(model.getDate());

                final String list_uid = getRef(position).getKey();
                userref.child(list_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        String userimage = dataSnapshot.child("Image").getValue().toString();
                        userimager = userimage;
                        if(dataSnapshot.hasChild("online")) {
                            holder.usernameid.setText(username);
                            holder.setimage(userimage);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                int pos = holder.getAdapterPosition();
                final String usid = getRef(pos).getKey();

                DatabaseReference dbb =FirebaseDatabase.getInstance().getReference().child("Groups").child(groupname).child("members");

                dbb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(usid)){
                            holder.cb.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        String uid = getRef(pos).getKey();
                        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();

                        if(!holder.cb.isChecked()){
                            holder.cb.setChecked(true);
                            Toast.makeText(Groupmembers.this,uid,Toast.LENGTH_SHORT).show();

                            rootref.child("Groups").child(groupname).child("members").child(uid).child("admin").setValue(".");
                            rootref.child("users").child(uid).child("Groups").child(groupname).child("groupname").setValue(groupname);

                        }

                        else if(holder.cb.isChecked()) {
                            holder.cb.setChecked(false);
                            rootref.child("Groups").child(groupname).child("members").child(uid).removeValue();
                            rootref.child("users").child(uid).child("Groups").child(groupname).child("groupname").removeValue();

                        }


                    }
                });



            }

            @NonNull
            @Override
            public addgroupmembersholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groupmembers, viewGroup, false);
                return new addgroupmembersholder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        friendlist.setAdapter(firebaseRecyclerAdapter);
    }

    public class addgroupmembersholder extends RecyclerView.ViewHolder{

        View mview;

        TextView date,usernameid;
        CircleImageView image_f;
        CheckBox cb;



        public addgroupmembersholder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            date = itemView.findViewById(R.id.gm_single_status);

            cb= itemView.findViewById(R.id.cb);
            usernameid = itemView.findViewById(R.id.gm_single_name);



        }

        public void setimage(String image) {
            image_f = itemView.findViewById(R.id.gm_single_image);
            Picasso.get().load(image).placeholder(R.drawable.man).into(image_f);

        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

}


