package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Groupchatactivity extends AppCompatActivity {

    EditText gmsg;

    ImageButton eg;

    EditText gm;

    RelativeLayout rootView;

    String groupname;

    ProgressDialog loading;

    Uri fileuri;

    String fileurl="";

    StorageTask uploadtask;

    String check=null;

    FirebaseAuth auth;

    ImageButton f;
    String currentDate;

    String currentTime;

    ImageButton s;

    DatabaseReference rootref;

    DatabaseReference groupref;

    List<Groupmessages> messagesList = new ArrayList<>();


    Groupmessageadapter madapter;

    ImageButton vrg;




    RecyclerView groupmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchatactivity);

        groupname = getIntent().getStringExtra("group_name");

        rootref = FirebaseDatabase.getInstance().getReference();



        gmsg = findViewById(R.id.groupmessage);



        madapter = new Groupmessageadapter(messagesList);

        eg = findViewById(R.id.sendemojigrp);

        rootView= findViewById(R.id.rvg);


        s = findViewById(R.id.send_button);

        loading = new ProgressDialog(Groupchatactivity.this);

        auth = FirebaseAuth.getInstance();

        groupref = FirebaseDatabase.getInstance().getReference().child("Groups");
        groupref.keepSynced(true);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

         currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


        f=findViewById(R.id.addfilegrp);


        groupmessage = findViewById(R.id.group_messagesrv);
        groupmessage.setHasFixedSize(true);
        groupmessage.setLayoutManager(new LinearLayoutManager(this));
        groupmessage.setAdapter(madapter);
        groupmessage.smoothScrollToPosition(madapter.getItemCount());

        vrg = findViewById(R.id.vrg);

        vrg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                check="voicemessage";
                Intent i = new Intent(Groupchatactivity.this,voicerecordinggroup.class);
                i.putExtra("groupname",groupname);
                startActivity(i);

                return false;
            }
        });

        eg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(gmsg);
                emojiPopup.toggle(); // Toggles visibility of the Popup.
                emojiPopup.dismiss(); // Dismisses the Popup.
                emojiPopup.isShowing(); // Returns true when Popup is showing.
            }
        });

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{

                        "Images",
                        "Pdf files",
                        "Video"

                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Groupchatactivity.this);
                builder.setTitle("Select the file");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which ==0)
                        {
                            check ="image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"Select image"),43);

                        }
                        if (which ==1)

                        {
                            check="pdf";
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(i,"Select file"),43);

                        }
                        if(which==2){
                            check="video";
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType(("video/*"));
                            startActivityForResult(Intent.createChooser(i,"Select small video file "),43);
                        }

                    }
                });
                builder.show();
            }
        });

        loadmessages();



        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               DatabaseReference group_message_push = groupref.child(groupname).child("messages").push();
                String push_id = group_message_push.getKey();

                String message = gmsg.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(Groupchatactivity.this,"Message can not be empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    Map sm = new HashMap();
                    sm.put("message",message);
                    sm.put("from",auth.getCurrentUser().getUid());
                    sm.put("time",currentTime);
                    sm.put("date",currentDate);
                    sm.put("type","text");

                    group_message_push.updateChildren(sm).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            gmsg.setText("");
                        }
                    });

                }

            }
        });
    }

    private void loadmessages() {

        groupref.child(groupname).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Groupmessages groupmessages = dataSnapshot.getValue(Groupmessages.class);
                messagesList.add(groupmessages);

                madapter.notifyDataSetChanged();

                groupmessage.smoothScrollToPosition(madapter.getItemCount());



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==43 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            fileuri = data.getData();
            if(!check.equals("image")){

                loading.setTitle("file uploading");
                loading.show();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Groups").child(groupname).child("Documentfiles");
                DatabaseReference user_message_push = groupref.child("messages").push();
                user_message_push.keepSynced(true);

                final String push_id = user_message_push.getKey();

                final StorageReference fileref = storageReference.child(push_id+"."+check);
                fileref.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileurl = uri.toString();

                                Map messagemap = new HashMap();
                                messagemap.put("message",fileurl);
                                messagemap.put("name",fileuri.getLastPathSegment());
                                messagemap.put("seen",false);
                                messagemap.put("type",check);
                                messagemap.put("time",currentTime);
                                messagemap.put("from",auth.getCurrentUser().getUid());


                                Map messageusermap = new HashMap();
                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);
                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);


                                rootref.updateChildren(messageusermap);

                                loading.dismiss();


                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        int d = (int) p;
                        loading.setMessage(d+"%"+"  uploading...");
                    }
                });

            }

            else if(check.equals("image")){

                loading.setTitle("image uploading");
                loading.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Groups").child(groupname).child("imagefiles");

                DatabaseReference user_message_push = groupref.child("messages").push();
                user_message_push.keepSynced(true);

                final String push_id = user_message_push.getKey();

                final StorageReference fileref = storageReference.child(push_id+"."+check);


                fileref.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String fileurl = uri.toString();

                                Map messagemap = new HashMap();
                                messagemap.put("message",fileurl);
                                messagemap.put("name",fileuri.getLastPathSegment());
                                messagemap.put("seen",false);
                                messagemap.put("type",check);
                                messagemap.put("time",currentTime);
                                messagemap.put("from",auth.getCurrentUser().getUid());


                                Map messageusermap = new HashMap();
                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);
                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);


                                rootref.updateChildren(messageusermap);

                                loading.dismiss();


                            }
                        });
                    }
                });


            }
            else if(check.equals("video")){
                loading.setTitle("file uploading");
                loading.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Groups").child(groupname).child("videofiles");

                DatabaseReference user_message_push = groupref.child("messages").push();
                user_message_push.keepSynced(true);

                final String push_id = user_message_push.getKey();

                final StorageReference fileref = storageReference.child(push_id+"."+check);

                fileref.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileurl = uri.toString();

                                Map messagemap = new HashMap();
                                messagemap.put("message",fileurl);
                                messagemap.put("name",fileuri.getLastPathSegment());
                                messagemap.put("seen",false);
                                messagemap.put("type",check);
                                messagemap.put("time",currentTime);
                                messagemap.put("from",auth.getCurrentUser().getUid());


                                Map messageusermap = new HashMap();

                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);
                                messageusermap.put("Groups"+"/"+groupname+"/"+"messages"+"/"+push_id,messagemap);


                                rootref.updateChildren(messageusermap);

                                loading.dismiss();




                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double p = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        int d = (int) p;
                        loading.setMessage(d+"%"+"  uploading...");

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
