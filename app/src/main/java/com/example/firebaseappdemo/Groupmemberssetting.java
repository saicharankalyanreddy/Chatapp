package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class Groupmemberssetting extends AppCompatActivity {

    ImageButton next;
    CircleImageView gimg;
    TextView gn;
    String gintentname,gintentimg;

    DatabaseReference rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmemberssetting);

        rootref = FirebaseDatabase.getInstance().getReference();

        gintentname = getIntent().getStringExtra("gname");
        gintentimg=getIntent().getStringExtra("gimg");

        next = findViewById(R.id.nexts);

        gn = findViewById(R.id.groupnameedit);
        gimg = findViewById(R.id.Groupimageviewsetadmin);

        gn.setText(gintentname);

        Picasso.get().load(gintentimg).placeholder(R.drawable.gchat).into(gimg);

        gimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });

        final String nn = gn.getText().toString();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Groupmemberssetting.this,Groupactivity.class));
                finish();
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                Uri imguri = data.getData();
                File thumb_path = new File(imguri.getPath());
                final StorageReference fref = FirebaseStorage.getInstance().getReference().child("Groups/"+gintentimg+"/group.jpg");

                fref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                Picasso.get().load(uri).into(gimg);
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Groups").child(gintentname);
                                db.child("groupimage").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Groupmemberssetting.this, "Image uploaded", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setgroupadmin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.Editadmin){
            Intent i = new Intent(Groupmemberssetting.this,admin.class);
            i.putExtra("group_name",gintentname);
            startActivity(i);
            finish();

        }
        else if(id==R.id.editgroupmembers){
            Intent i = new Intent(Groupmemberssetting.this,Groupmembers.class);
            i.putExtra("group_name",gintentname);
            startActivity(i);
            finish();
        }



        return true;
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
