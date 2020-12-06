package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.xml.sax.DTDHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class Groupactivity extends AppCompatActivity {

    ImageButton addgroup;

    DatabaseReference rootref;

    RecyclerView groups_list;
    Groupmessageadapter madapter;




    private DatabaseReference GroupsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupactivity);


        rootref = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();


        GroupsRef =FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).child("Groups");

       // Retrivegroups();
       // Initializefields();

        groups_list = findViewById(R.id.list_groups);
        groups_list.setHasFixedSize(true);
        groups_list.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        groups_list.setAdapter(madapter);


    }






        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addgroup){
            final EditText groupname = new EditText(this);


            AlertDialog .Builder alert = new AlertDialog.Builder(this).setTitle("Create new group").setMessage("Group name")
                    .setView(groupname).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String groupName = groupname.getText().toString().trim();

                            if(TextUtils.isEmpty(groupName)){
                                Toast.makeText(Groupactivity.this,"Group name can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                CreateNewGroup(groupName);


                            }


                        }
                    });
            alert.show();

        }
        return true;


    }

    private void CreateNewGroup(final String groupName)
    {

        DatabaseReference dbb = FirebaseDatabase.getInstance().getReference();
        dbb.child("Groups").child(groupName).child("groupimage").setValue(".");

        Intent setupg = new Intent(Groupactivity.this,Settingupgroup.class);
        setupg.putExtra("gname",groupName);

        startActivity(setupg);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        }
        FirebaseRecyclerOptions<groups> options = new FirebaseRecyclerOptions.Builder<groups>()
                .setQuery(GroupsRef,groups.class)
                .build();
        FirebaseRecyclerAdapter<groups, GrouplistViewHolder> adapter=new FirebaseRecyclerAdapter<groups,GrouplistViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final GrouplistViewHolder  grouplistViewHolder, final int i, @NonNull final groups model) {


                String gn = model.getGroupname();

                grouplistViewHolder.groupName.setText(gn);



                DatabaseReference dbb = FirebaseDatabase.getInstance().getReference();
                dbb.child("Groups").child(gn).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String img = dataSnapshot.child("groupimage").getValue().toString();
                       grouplistViewHolder.setimage(img);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                grouplistViewHolder.groupName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String group_name=getRef(i).getKey();
                                        Intent intent=new Intent(Groupactivity.this,Groupchatactivity.class);
                                        intent.putExtra("group_name",group_name);
                                        startActivity(intent);

                                    }
                                }
                                );
                              grouplistViewHolder.gi.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {




                                      final DatabaseReference dbb = FirebaseDatabase.getInstance().getReference();


                                      final String group_name=getRef(i).getKey();

                                      FirebaseAuth auth = FirebaseAuth.getInstance();



                                      dbb.child("Groups").child(group_name).child("members").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                             String admincheck =  dataSnapshot.child("admin").getValue().toString();
                                              if(admincheck.equals("admin")){

                                                  dbb.child("Groups").child(group_name).addValueEventListener(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                          final String imagega= dataSnapshot.child("groupimage").getValue().toString();
                                                          Intent i = new Intent(Groupactivity.this,Groupmemberssetting.class);
                                                          i.putExtra("gname",group_name);
                                                          i.putExtra("gimg",imagega);
                                                          startActivity(i);
                                                          finish();

                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                                      }
                                                  });
                                              }
                                              else {
                                                  Toast.makeText(Groupactivity.this,"You are not an Admin",Toast.LENGTH_SHORT).show();
                                              }
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });




                                  }
                              });


            }
            public GrouplistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groups_display_layout, viewGroup, false);
                return new GrouplistViewHolder(view);
            }
        };

        groups_list.setAdapter(adapter);
        adapter.startListening();
    }
    public static class  GrouplistViewHolder extends RecyclerView.ViewHolder{
        TextView groupName;
        CircleImageView gi;
        public GrouplistViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName =itemView.findViewById(R.id.group_name);
            gi = itemView.findViewById(R.id.gimg);
        }


        public void setimage(String groupimage) {

            Picasso.get().load(groupimage).placeholder(R.drawable.gchat).into(gi);
        }
    }






    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }



}
