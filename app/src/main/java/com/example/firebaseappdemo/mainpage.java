package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class mainpage extends AppCompatActivity {

    TextView welmsg;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser fuser;


    ImageButton ib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        welcomemsg();

        emailverification();

        ib = findViewById(R.id.ib1);


        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainpage.this, Settingupprofile.class);
                startActivity(i);


            }
        });


    }

    void welcomemsg() {
        welmsg = findViewById(R.id.welcome);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                welmsg.setText("Welcome \n" + documentSnapshot.getString("username"));

            }
        });

    }


    void emailverification() {
        fuser = auth.getCurrentUser();
        if (!(fuser.isEmailVerified())) {
            AlertDialog.Builder al = new AlertDialog.Builder(this).setMessage("Email not verified").setCancelable(false)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(mainpage.this, MainActivity.class));
                            finish();
                        }
                    }).setNegativeButton("Get link again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mainpage.this, "Email has sent for verification", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mainpage.this, "Error sending the email check your network connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            AlertDialog a = al.create();
            a.show();

        }


    }
}


