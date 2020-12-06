package com.example.firebaseappdemo;

import android.app.Application;
import android.content.Intent;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.core.provider.FontRequest;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

public class Loggedin extends Application {

    FirebaseAuth auth;
    DatabaseReference mdatabe;
    @Override
    public void onCreate() {
        super.onCreate();
        EmojiManager.install(new TwitterEmojiProvider());
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        final FirebaseUser muser = firebaseAuth.getCurrentUser();
        if(muser != null){

            auth = FirebaseAuth.getInstance();
            mdatabe = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

            mdatabe.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if(dataSnapshot != null) {

                        mdatabe.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





        }




    }

}
