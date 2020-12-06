package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Voicemessage extends AppCompatActivity {

    String mc;

    FirebaseAuth auth;

    DatabaseReference rootref;

    StorageReference st = FirebaseStorage.getInstance().getReference();
    static final String LOG_TAG = "AudioRecordTest";
    static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    static String fileName = null;

    RecordButton recordButton = null;
    MediaRecorder recorder = null;

    private PlayButton   playButton = null;
    private MediaPlayer   player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;

    }

    void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fileName);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Uri uri = Uri.fromFile(new File(fileName));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Recordingfiles");

        final String current_user_ref = "messages/"+auth.getCurrentUser().getUid()+"/"+mc;
        final String chat_user_ref = "messages/"+mc+"/"+auth.getCurrentUser().getUid();

        DatabaseReference user_message_push = rootref.child("messages")
                .child(auth.getCurrentUser().getUid()).child(mc).push();

        final String push_id = user_message_push.getKey();

        final StorageReference fileref = storageReference.child(push_id+"."+"3gpp");

        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String fileurl = uri.toString();

                        Map messagemap = new HashMap();
                        messagemap.put("message",fileurl);
                        messagemap.put("name",uri.getLastPathSegment());
                        messagemap.put("seen",false);
                        messagemap.put("type","audiorec");
                        messagemap.put("time", ServerValue.TIMESTAMP);
                        messagemap.put("from",auth.getCurrentUser().getUid());


                        Map messageusermap = new HashMap();
                        messageusermap.put(current_user_ref+"/"+push_id,messagemap);
                        messageusermap.put(chat_user_ref+"/"+push_id,messagemap);


                        rootref.updateChildren(messageusermap);



                        finish();




                    }
                });
            }
        });

    }

    class RecordButton extends androidx.appcompat.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Voicemessage ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends androidx.appcompat.widget.AppCompatButton {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Voicemessage ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }



    ImageButton rec;

    TextView cr;

    String checkrec = "recording";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_voicemessage);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();

        fileName += "/audiorecordtest.mp3";
        rec= findViewById(R.id.record);
        auth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();

        mc = getIntent().getStringExtra("chatuser");

        cr = findViewById(R.id.recetxt);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkrec .equals("recording")){
                    startRecording();
                    checkrec = "stopped";
                    cr.setText("Recording started..");

                }

                else if(checkrec.equals("stopped")){
                    stopRecording();
                    checkrec="recording";
                    cr.setText("Please wait...message sending ");
                }
            }
        });





    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        }
    }
}


