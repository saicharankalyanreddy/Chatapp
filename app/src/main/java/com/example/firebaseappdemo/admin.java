package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class admin extends AppCompatActivity {

    RecyclerView adminlist;

    DatabaseReference gref;

    String ginn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminlist = findViewById(R.id.adminlist);

        ginn = getIntent().getStringExtra("group_name");

        adminlist.setHasFixedSize(true);
        adminlist.setLayoutManager(new LinearLayoutManager(this));





    }

    @Override
    protected void onStart() {
        super.onStart();

        ginn = getIntent().getStringExtra("group_name");

        gref = FirebaseDatabase.getInstance().getReference().child("Groups").child(ginn).child("members");


        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(gref,Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends,avholder> adapter = new FirebaseRecyclerAdapter<Friends, avholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final avholder holder, int position, @NonNull Friends model) {



                final String list_uid = getRef(position).getKey();



                gref.child(list_uid).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String adminc = dataSnapshot.getValue().toString();

                        if(adminc.equals("admin")){
                            holder.reject.setVisibility(View.GONE);
                            holder.accept.setVisibility(View.GONE);
                        }

                        else {

                            holder.reject.setBackgroundResource(R.drawable.senderbackground);

                            holder.reject.setText("Make admin");
                            holder.accept.setText("Remove from group");
                            holder.accept.setBackgroundResource(R.drawable.rejectbutton);

                        }

                        FirebaseDatabase.getInstance().getReference().child("users").child(list_uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String username = dataSnapshot.child("username").getValue().toString();
                                String userimage = dataSnapshot.child("Image").getValue().toString();

                                if(dataSnapshot.hasChild("online")) {
                                    holder.tv_req_users_username.setText(username);
                                    holder.setimage(userimage);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder.reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String am = "Once you do it you can not undo it";

                                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("You really want to do it?");
                                builder.setMessage("Once you do it you can not undo it");
                                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        gref.child(list_uid).child("admin").setValue("admin");

                                        Toast.makeText(holder.itemView.getContext(),"User is admin now",Toast.LENGTH_SHORT).show();

                                        holder.reject.setVisibility(View.GONE);
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();


                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        FirebaseDatabase.getInstance().getReference().child("Groups").child(ginn).child("members").child(list_uid).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(list_uid).child("Groups").child(ginn).child("groupname").removeValue();
                        Toast.makeText(holder.itemView.getContext(),"User removed from group",Toast.LENGTH_SHORT).show();


                    }
                });



            }

            @NonNull
            @Override
            public avholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_single,parent,false);
                return new avholder(v);
            }
        };

        adminlist.setAdapter(adapter);
        adapter.startListening();
    }

    class avholder extends RecyclerView.ViewHolder{

        View mview;
        TextView tv_req_users_username ;//, tv_all_users_status;
        CircleImageView iv_req_users_image;
        Button accept,reject;

        public avholder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            tv_req_users_username = itemView.findViewById(R.id.req_single_name);
            iv_req_users_image = itemView.findViewById(R.id.req_single_image);

            accept = itemView.findViewById(R.id.acceptreq);

            reject = itemView.findViewById(R.id.rejreq);
        }

        public void setimage(String userimage) {

            Picasso.get().load(userimage).into(iv_req_users_image);
        }
    }
}
