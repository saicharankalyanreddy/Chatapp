package com.example.firebaseappdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profileactivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView mail,pass,name,phone,statusdp;

    FloatingActionButton epfab;


    DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        auth = FirebaseAuth.getInstance();

        userref =  FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        statusdp = findViewById(R.id.statusdisplay);

        epfab = findViewById(R.id.editprofilefab);
        final ImageView pc = findViewById(R.id.profilepic);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.man).into(pc);
            }
        });

        epfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profileactivity.this,profileupdate.class));
            }
        });


    }
    void Showprofile()
    {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.emailtv);
        phone = findViewById(R.id.phonetv);
        name = findViewById(R.id.usern);


        String uid = auth.getCurrentUser().getUid();
        final DocumentReference documentReference = firestore.collection("users").document(uid);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                mail.setText(documentSnapshot.getString("Email"));
                name.setText(documentSnapshot.getString("username"));
                phone.setText(documentSnapshot.getString("Phonenumber"));
                statusdp.setText(documentSnapshot.getString("status"));
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        Showprofile();
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
