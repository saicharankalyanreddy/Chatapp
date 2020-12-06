package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Settingupprofile extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    EditText unmeedit;

    DatabaseReference userref;



    EditText status;

    ImageView imgp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingupprofile);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        imgp = findViewById(R.id.imgprofile);

        unmeedit = findViewById(R.id.unme);

        status = findViewById(R.id.statusup1);
        String uid = auth.getCurrentUser().getUid();
        DocumentReference d = firestore.collection("users").document(uid);
        d.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                unmeedit.setText(documentSnapshot.getString("username"));
            }
        });


        ImageButton done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edunm = unmeedit.getText().toString();

                String statusstr = status.getText().toString();
                DocumentReference d = firestore.collection("users").document(auth.getCurrentUser().getUid());
                DatabaseReference dbs = firebaseDatabase.getReference().child("users").child(auth.getCurrentUser().getUid());
                Map<String,Object> m = new HashMap<>();
                m.put("username",edunm);
                m.put("status",statusstr);
                d.update(m);
                dbs.updateChildren(m);
                Intent intent = new Intent(Settingupprofile.this,Chatactivity.class);
                startActivity(intent);

            }
        });




        ppicchange();


    }

    void ppicchange()
    {

        imgp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1000);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000)
        {
            if(resultCode==RESULT_OK)

            {
                final ProgressDialog p = new ProgressDialog(Settingupprofile.this);
                p.setMessage("Please wait..");
                p.setTitle("Image uploading");
                p.show();
                Uri uri = data.getData();
                final StorageReference fref = FirebaseStorage.getInstance().getReference().child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
                fref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                Picasso.get().load(uri).into(imgp);
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                                Map<String,Object> imgurl = new HashMap<>();
                                imgurl.put("Image",String.valueOf(uri));
                                DocumentReference d = firestore.collection("users").document(auth.getCurrentUser().getUid());

                                d.update(imgurl);
                                db.updateChildren(imgurl);

                                Toast.makeText(Settingupprofile.this,"Image uploaded",Toast.LENGTH_SHORT).show();
                                p.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settingupprofile.this,"Upload filed",Toast.LENGTH_SHORT).show();
                    }
                });

            }
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
