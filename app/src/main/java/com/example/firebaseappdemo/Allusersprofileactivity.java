package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Allusersprofileactivity extends AppCompatActivity {


    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView n,s;
    ImageView i;

    Button req,dec;

    DatabaseReference mfrendreq;
    DatabaseReference mfreinds;
    DatabaseReference mnotfication;

    DatabaseReference userref;

    CollectionReference frendreq = FirebaseFirestore.getInstance().collection("users");

    String current_state;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusersprofileactivity);

        current_state= "not_friends";//Not a friend

        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        final FirebaseUser cuser =auth.getCurrentUser();

        mfrendreq = FirebaseDatabase.getInstance().getReference().child("Friend_req");


        mfreinds = FirebaseDatabase.getInstance().getReference().child("Friends");


        mnotfication = FirebaseDatabase.getInstance().getReference().child("Nofications");


        dec = findViewById(R.id.reqdec);
        dec.setVisibility(View.INVISIBLE);
        dec.setEnabled(false);



        final String userid =getIntent().getStringExtra("Userid");

        s = findViewById(R.id.user_id_status);
        i= findViewById(R.id.alluserppic);

        n= findViewById(R.id.user_id_name);
        DocumentReference d = fstore.collection("users").document(userid);




        d.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                n.setText(documentSnapshot.getString("username"));
                s.setText(documentSnapshot.getString("status"));

                final String imgurl = documentSnapshot.getString("Image");
                if(imgurl.equals("default")){
                    Picasso.get().load(imgurl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.man).into(i, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imgurl).placeholder(R.drawable.man).into(i);
                        }
                    });
                }
                else{
                    Picasso.get().load(imgurl).placeholder(R.drawable.man).into(i);
                }



            }
        });



        final ProgressDialog p = new ProgressDialog(Allusersprofileactivity.this);
        p.setTitle("Loading... ");
        p.setMessage("Please wait");
        p.show();

        final DatabaseReference mfbdb = firebaseDatabase.getReference().child("users");
        mfbdb.keepSynced(true);

        mfbdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference dbb = mfbdb.child("Friend_req");
                dbb.keepSynced(true);

                // ------Friends and Request feature

            mfrendreq.child(cuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(userid)){
                        String reque = dataSnapshot.child(userid).child("req_type").getValue().toString();
                        if(reque.equals("received")){
                            current_state = "req_recieved";
                            req.setText("Accept Friend request");
                            dec.setVisibility(View.VISIBLE);
                            dec.setEnabled(true);
                        }
                        else if(reque.equals("sent")){
                            current_state = "req_sent";
                            req.setText("Cancel Friend request");
                        }
                    }

                    else {
                        mfreinds.child(cuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid)){
                                    current_state = "friends";
                                    req.setText("Unfriend");

                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        req = findViewById(R.id.reqsend);
        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                req.setEnabled(false);
                if(current_state.equals("not_friends")) {
                    mfrendreq.child(cuser.getUid()).child(userid).child("req_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mfrendreq.child(userid).child(cuser.getUid()).child("req_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Map<String,Object> notificationdata = new HashMap<>();
                                        notificationdata.put("from",cuser.getUid());
                                        notificationdata.put("type","request");
                                        mnotfication.child(userid).push().setValue(notificationdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                req.setText("Cancel Friend request");


                                                Toast.makeText(Allusersprofileactivity.this, " Request sent", Toast.LENGTH_SHORT).show();

                                                current_state = "req_sent";
                                            }
                                        });


                                    }
                                });
                            } else {
                                Toast.makeText(Allusersprofileactivity.this, "Failed sending Request", Toast.LENGTH_SHORT).show();
                            }
                            req.setEnabled(true);
                        }
                    });
                }

                    //   Cancel Friend request

                    if(current_state.equals("req_sent"))
                    {
                        mfrendreq.child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mfrendreq.child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        req.setEnabled(true);
                                        current_state = "not_friends";
                                        req.setText("Send Friend Request");

                                        Toast.makeText(Allusersprofileactivity.this,"Cancelled friend request",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    //Accept Friend Request
                    if(current_state.equals("req_recieved")){

                        final String ctime = DateFormat.getDateTimeInstance().format(new Date());
                        mfreinds.child(cuser.getUid()).child(userid).child("date").setValue(ctime).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            mfreinds.child(userid).child(cuser.getUid()).child("date").setValue(ctime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mfrendreq.child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        mfrendreq.child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                req.setEnabled(true);
                                                current_state = "friends";
                                                req.setText("Unfriend");
                                                dec.setVisibility(View.INVISIBLE);
                                                dec.setEnabled(false);
                                            }
                                        });
                                        }
                                    });
                                }
                            });
                            }
                        });
                    }

                    //Un friend
                    if(current_state.equals("friends")){
                        mfreinds.child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mfreinds.child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference().child("chat").child(userid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    FirebaseDatabase.getInstance().getReference().child("chat").child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference().child("chat").child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    FirebaseDatabase.getInstance().getReference().child("messages").child(userid).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if(dataSnapshot.exists()){
                                                                                FirebaseDatabase.getInstance().getReference().child("messages").child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        FirebaseDatabase.getInstance().getReference().child("messages").child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {

                                                                                                req.setEnabled(true);
                                                                                                current_state = "not_friends";
                                                                                                req.setText("Send Friend request");

                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                            else {

                                                                                req.setEnabled(true);
                                                                                current_state = "not_friends";
                                                                                req.setText("Send Friend request");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                                }
                                                            });
                                                        }
                                                    });


                                                }
                                                else {
                                                    req.setEnabled(true);
                                                    current_state = "not_friends";
                                                    req.setText("Send Friend request");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });



                                    }
                                });
                            }
                        });
                    }



            }
        });
        p.dismiss();

        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mfrendreq.child(userid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mfrendreq.child(cuser.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            req.setText("Send Friend Request");
                            dec.setVisibility(View.INVISIBLE);
                            dec.setEnabled(false);
                            current_state="not_friends";
                        }
                    });
                }
            });
            }
        });

        if(cuser.getUid().equals(userid))
        {
            req.setVisibility(View.INVISIBLE);
            req.setEnabled(false);
            dec.setEnabled(false);
            dec.setVisibility(View.INVISIBLE);

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
