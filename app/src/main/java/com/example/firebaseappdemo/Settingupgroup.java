package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settingupgroup extends AppCompatActivity {

    CircleImageView gimg;
    TextView gn;


    ImageButton done;
    DatabaseReference groupref;
    FirebaseAuth fauth;
    StorageReference storageReference;

    DatabaseReference rootref;


    String ogn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingupgroup);

        groupref = FirebaseDatabase.getInstance().getReference().child("Groups");

        rootref = FirebaseDatabase.getInstance().getReference();


        ogn = getIntent().getStringExtra("gname");

        storageReference = FirebaseStorage.getInstance().getReference();

        fauth = FirebaseAuth.getInstance();


        gimg = findViewById(R.id.groupimage);
        gn = findViewById(R.id.gname);

        done = findViewById(R.id.doneg);

        gn.setText(ogn);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                rootref.child("Groups").child(ogn).child("groupname").setValue(ogn)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    rootref.child("Groups").child(ogn).child("members").child(auth.getCurrentUser().getUid()).child("admin").setValue("admin");
                                   rootref.child("users").child(auth.getCurrentUser().getUid()).child("Groups").child(ogn).child("groupname").setValue(ogn);
                                    Toast.makeText(Settingupgroup.this, ogn + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                Intent i = new Intent(Settingupgroup.this, Groupmembers.class);
                i.putExtra("group_name",ogn);




                startActivity(i);

                finish();


            }
        });

        gimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);

            }
        });


      /*  gimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1000);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();
                Picasso.get().load(uri).into(gimg);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("groups").child(ogn);
                Map<String, Object> imgurl = new HashMap<>();
                imgurl.put("groupimage", String.valueOf(uri));
                db.updateChildren(imgurl);

            }
        }
    */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                Uri imguri = data.getData();
                File thumb_path = new File(imguri.getPath());
                final StorageReference fref = storageReference.child("Groups/"+ogn+"/group.jpg");

                fref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                Picasso.get().load(uri).into(gimg);
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Groups").child(ogn);
                                db.child("groupimage").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Settingupgroup.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

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
