package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Compressor;

public class profileupdate extends AppCompatActivity {

    EditText uemail,uphone,uname,statusup;
    Button ubtn;
    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    FirebaseFirestore fstore;

    DatabaseReference userref;

    ImageView profilepic;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    StorageReference pref = storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileupdate);

        FirebaseAuth auth = FirebaseAuth.getInstance();


        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        uprofile();
        profilepic = findViewById(R.id.profileimg);
        pref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Picasso.get().load(uri).placeholder(R.drawable.man).into(profilepic);
            }
        });

        statusup = findViewById(R.id.statusupdate2);


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengaller = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(opengaller,1000);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                final Uri imguri = data.getData();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

               // profilepic.setImageURI(imguri);
                File thumb_path = new File(imguri.getPath());
                final StorageReference fref = storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");





                fref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profilepic);
                                Map<String,Object> img = new HashMap<>();
                                img.put("Image",String.valueOf(uri));
                                DocumentReference d = fstore.collection("users").document(fauth.getCurrentUser().getUid());
                                d.update(img);

                                DatabaseReference databaseReference = firebaseDatabase.child("users").child(fauth.getCurrentUser().getUid());

                                databaseReference.updateChildren(img);

                            }
                        });

                        Toast.makeText(profileupdate.this,"Image uploaded",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profileupdate.this,"Upload filed",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    void uprofile()
    {
        uemail = findViewById(R.id.updateemail);
        uphone= findViewById(R.id.updatephon);
        uname = findViewById(R.id.updatename);
        ubtn = findViewById(R.id.updatevalues);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser fuser = fauth.getCurrentUser();
        String uid = fuser.getUid();
        DocumentReference d = fstore.collection("users").document(uid);
        d.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                uemail.setText(documentSnapshot.getString("Email"));
                uphone.setText(documentSnapshot.getString("Phonenumber"));
                uname.setText(documentSnapshot.getString("username"));
                statusup.setText(documentSnapshot.getString("status"));
            }
        });
        upp();

    }

    void upp()
    {
        ubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog p = new ProgressDialog(profileupdate.this);
                p.setTitle("Profile update");
                p.setMessage("Please wait");
                p.show();
                final String upemail= uemail.getText().toString();
                final String upphone= uphone.getText().toString();
                final String upname= uname.getText().toString();
                final String upstatus = statusup.getText().toString();
                FirebaseUser fuser = fauth.getCurrentUser();
                final DatabaseReference db =firebaseDatabase.child("users").child(fauth.getCurrentUser().getUid());





                fuser.updateEmail(upemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String,Object> mm = new HashMap<>();
                        mm.put("Email",upemail);
                        mm.put("Phonenumber",upphone);
                        mm.put("username",upname);
                        mm.put("status",upstatus);
                        String userid = fauth.getCurrentUser().getUid();
                        DocumentReference dp = fstore.collection("users").document(userid);
                        db.updateChildren(mm);
                        dp.update(mm).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profileupdate.this,"Data updated",Toast.LENGTH_SHORT).show();
                                p.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                p.dismiss();
                                Toast.makeText(profileupdate.this,"Can not be updated",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

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
