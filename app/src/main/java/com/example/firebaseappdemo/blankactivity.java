package com.example.firebaseappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class blankactivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();


    FirebaseUser muser = firebaseAuth.getCurrentUser();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blankactivity);
        Loggedin l = new Loggedin();

        if(muser != null)
        {
            Intent i = new Intent(this,Chatactivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();


        }
        else {
            Intent intent = new Intent(this,loginactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


    }
}
