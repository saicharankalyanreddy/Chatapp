package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import android.view.Menu;
import android.view.MenuItem;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.material.tabs.TabLayout;




import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ServerTimestamp;


public class Chatactivity extends AppCompatActivity {

    TabLayout tabLayout ;

    EditText txtnme;

    DatabaseReference userref;

    CircleImageView imagesmall;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);

        auth = FirebaseAuth.getInstance();


        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());



        imagesmall = findViewById(R.id.smallimage);

        userref.child("online").setValue(true);

        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String img = dataSnapshot.child("Image").getValue().toString();
                Picasso.get().load(img).into(imagesmall);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("REQUESTS"));
        tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
        tabLayout.addTab(tabLayout.newTab().setText("FRIENDS"));

        final ViewPager viewPager = findViewById(R.id.viewpager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1,false);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getuname();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        }
        emailverification();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }



    void getuname()
    {
        final TextView nmetxt = findViewById(R.id.nmetxt);
        FirebaseAuth auth;
        FirebaseFirestore firestore;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("users").document(auth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                nmetxt.setText(documentSnapshot.getString("username"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainpagemenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {

            case R.id.profileitem :
                startActivity(new Intent(Chatactivity.this,Profileactivity.class));
                break;
            case R.id.editprofileitem:
                startActivity(new Intent(Chatactivity.this,profileupdate.class));
                break;
            case R.id.lgout:
                AlertDialog.Builder Logoutalert = new AlertDialog.Builder(Chatactivity.this)
                        .setMessage("Do you want to logout?").setTitle("Logout!!").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userref.child("online").setValue(ServerValue.TIMESTAMP);
                                userref.child("device_token").setValue("");
                               FirebaseAuth.getInstance().signOut();

                                startActivity(new Intent(Chatactivity.this, MainActivity.class));
                                finish();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alertDialog = Logoutalert.create();
                alertDialog.show();
                break;
            case R.id.allusers:
                startActivity(new Intent(Chatactivity.this,Allusersactivity.class));
                break;





            case R.id.groupsitem:
                startActivity(new Intent(Chatactivity.this,Groupactivity.class));


        }
        return true;
    }
    void emailverification() {
       final FirebaseUser fuser = auth.getCurrentUser();
        if(!(fuser.isEmailVerified()))
        {
            AlertDialog.Builder al = new AlertDialog.Builder(this).setMessage("Email not verified").setCancelable(false)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity( new Intent(Chatactivity.this,MainActivity.class));
                            finish();
                        }
                    }).setNegativeButton("Get link again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    Toast.makeText(Chatactivity.this,"Email has sent for verification",Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Chatactivity.this,"Please Re login if you are done verfication!!",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Chatactivity.this,"Error sending the email check your network connection",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            AlertDialog a = al.create();
            a.show();

        }



    }

}
