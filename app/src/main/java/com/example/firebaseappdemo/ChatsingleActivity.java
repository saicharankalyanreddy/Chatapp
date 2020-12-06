package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentTransform;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsingleActivity extends AppCompatActivity {

String mchatuser;
ImageButton imageButton;
Toolbar toolbar;

ImageButton vrr;

    static String fileName = null;

    Voicemessage.RecordButton recordButton = null;
    MediaRecorder recorder = null;

RelativeLayout rootView;
ProgressDialog loading;
DatabaseReference rootref;

String check="";

String userimage;

EditText messagetext;


Uri fileuri;

String fileurl="";

StorageTask uploadtask;



TextView dn,ls;

CircleImageView cimgs;

FirebaseAuth auth;

ImageButton send,file;

RecyclerView mmessages_list;

List<Messages> messagesList = new ArrayList<>();

LinearLayoutManager mLinearlayout;

Messageadapter madapter;





    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatsingle);

        mchatuser = getIntent().getStringExtra("Userid");


        userimage = getIntent().getStringExtra("Userimage");

        messagetext = findViewById(R.id.chat_text);

        send = findViewById(R.id.btn_send);

        vrr = findViewById(R.id.vr);

        file = findViewById(R.id.addfile);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);


        rootref = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview = inflater.inflate(R.layout.toolbarchat,null);

        actionBar.setCustomView(actionbarview);


        dn = findViewById(R.id.dnn);
        ls = findViewById(R.id.ls);

        imageButton = findViewById(R.id.sendemoji);

        loading = new ProgressDialog(ChatsingleActivity.this);

        final Voicemessage vm = new Voicemessage();



        cimgs = findViewById(R.id.cimg);

        rootView= findViewById(R.id.rootview);

        madapter = new Messageadapter(messagesList);

        mmessages_list = findViewById(R.id.messages_list);


        mLinearlayout = new LinearLayoutManager(this);
        mmessages_list.setHasFixedSize(true);
        mmessages_list.setLayoutManager(mLinearlayout);

        mmessages_list.setAdapter(madapter);

        mmessages_list.smoothScrollToPosition(madapter.getItemCount());

        vrr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                check="voicemessage";
                Intent i = new Intent(ChatsingleActivity.this,Voicemessage.class);
                i.putExtra("chatuser",mchatuser);
                startActivity(i);

                return false;
            }
        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messagetext);
                emojiPopup.toggle(); // Toggles visibility of the Popup.
                emojiPopup.dismiss(); // Dismisses the Popup.
                emojiPopup.isShowing(); // Returns true when Popup is showing.
            }
        });



        loadmessages();

        //--- retrieving the details on toolbar

        rootref.child("users").child(mchatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String chat_username = dataSnapshot.child("username").getValue().toString();
                String chat_image = dataSnapshot.child("Image").getValue().toString();
                String ts = dataSnapshot.child("online").getValue().toString();


                dn.setText(chat_username);
                Picasso.get().load(chat_image).placeholder(R.drawable.man).into(cimgs);



                if(ts.equals("true")){
                    ls.setText("Online");

                }
                else {
                    Timeago timeago = new Timeago();
                    long lasttime = Long.parseLong(ts);
                    String lastseen = timeago.getTimeAgo(lasttime,getApplicationContext());
                    ls.setText(lastseen);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //---- Adding seen and timestamp for chat

        rootref.child("chat").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mchatuser))
                {
                   Map chataddmap = new HashMap();
                   chataddmap.put("seen",false);
                   chataddmap.put("timestamp", ServerValue.TIMESTAMP);

                   Map chatusermap = new HashMap();
                   chatusermap.put("chat/"+auth.getCurrentUser().getUid()+"/"+mchatuser,chataddmap);
                   chatusermap.put("chat/"+mchatuser+"/"+auth.getCurrentUser().getUid(),chataddmap);

                   rootref.updateChildren(chatusermap, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                           if(databaseError != null){
                               Log.d("Chat error",databaseError.getMessage());
                           }
                       }
                   });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //-------Sending message

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendmessage();

            }
        });


        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{

                        "Images",
                        "Pdf files",
                        "Video"

                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatsingleActivity.this);
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
                        if(which == 2)
                        {
                            check="video";
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType(("video/*"));
                            startActivityForResult(Intent.createChooser(i,"Select small video file "),43);
                        }

                        if(which == 3 ){
                        }

                    }
                });
                builder.show();
            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==43 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            fileuri = data.getData();
            if(check.equals("pdf")){

                loading.setTitle("file uploading");
                loading.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Documentfiles");

                final String current_user_ref = "messages/"+auth.getCurrentUser().getUid()+"/"+mchatuser;
                final String chat_user_ref = "messages/"+mchatuser+"/"+auth.getCurrentUser().getUid();

                DatabaseReference user_message_push = rootref.child("messages")
                        .child(auth.getCurrentUser().getUid()).child(mchatuser).push();

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
                                messagemap.put("time",ServerValue.TIMESTAMP);
                                messagemap.put("from",auth.getCurrentUser().getUid());


                                Map messageusermap = new HashMap();
                                messageusermap.put(current_user_ref+"/"+push_id,messagemap);
                                messageusermap.put(chat_user_ref+"/"+push_id,messagemap);


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

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagefiles");

                final String current_user_ref = "messages/"+auth.getCurrentUser().getUid()+"/"+mchatuser;
                final String chat_user_ref = "messages/"+mchatuser+"/"+auth.getCurrentUser().getUid();

                DatabaseReference user_message_push = rootref.child("messages")
                        .child(auth.getCurrentUser().getUid()).child(mchatuser).push();

                final String push_id = user_message_push.getKey();

                final StorageReference fileref = storageReference.child(push_id+"."+"jpg");

                uploadtask = fileref.putFile(fileuri);

                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return fileref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadurl = task.getResult();

                            fileurl = downloadurl.toString();


                            Map messagemap = new HashMap();
                            messagemap.put("message",fileurl);
                            messagemap.put("name",fileuri.getLastPathSegment());
                            messagemap.put("seen",false);
                            messagemap.put("type",check);
                            messagemap.put("time",ServerValue.TIMESTAMP);
                            messagemap.put("from",auth.getCurrentUser().getUid());


                            Map messageusermap = new HashMap();
                            messageusermap.put(current_user_ref+"/"+push_id,messagemap);
                            messageusermap.put(chat_user_ref+"/"+push_id,messagemap);

                            rootref.updateChildren(messageusermap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChatsingleActivity.this,"File sent",Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(ChatsingleActivity.this,"!!Error",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                    }
                });
            }
            else if(check.equals("video")){
                loading.setTitle("file uploading");
                loading.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Videofiles");

                final String current_user_ref = "messages/"+auth.getCurrentUser().getUid()+"/"+mchatuser;
                final String chat_user_ref = "messages/"+mchatuser+"/"+auth.getCurrentUser().getUid();

                DatabaseReference user_message_push = rootref.child("messages")
                        .child(auth.getCurrentUser().getUid()).child(mchatuser).push();

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
                                messagemap.put("time",ServerValue.TIMESTAMP);
                                messagemap.put("from",auth.getCurrentUser().getUid());


                                Map messageusermap = new HashMap();
                                messageusermap.put(current_user_ref+"/"+push_id,messagemap);
                                messageusermap.put(chat_user_ref+"/"+push_id,messagemap);


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
            else {
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            }

        }
    }




    private void loadmessages() {


        rootref.child("messages").child(auth.getCurrentUser().getUid()).child(mchatuser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                messagesList.add(message);
                madapter.notifyDataSetChanged();
                mmessages_list.smoothScrollToPosition(mmessages_list.getAdapter().getItemCount());


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

    void sendmessage(){
        String msg = messagetext.getText().toString();

        if(!TextUtils.isEmpty(msg)){

            String current_user_ref = "messages/"+auth.getCurrentUser().getUid()+"/"+mchatuser;
            String chat_user_ref = "messages/"+mchatuser+"/"+auth.getCurrentUser().getUid();

            DatabaseReference user_message_push = rootref.child("messages")
                    .child(auth.getCurrentUser().getUid()).child(mchatuser).push();

            String push_id = user_message_push.getKey();

            Map messagemap = new HashMap();
            messagemap.put("message",msg);
            messagemap.put("seen",false);
            messagemap.put("type","text");
            messagemap.put("time",ServerValue.TIMESTAMP);
            messagemap.put("from",auth.getCurrentUser().getUid());

            Map messageusermap = new HashMap();
            messageusermap.put(current_user_ref+"/"+push_id,messagemap);
            messageusermap.put(chat_user_ref+"/"+push_id,messagemap);

            rootref.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    messagetext.setText("");

                }
            });

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
