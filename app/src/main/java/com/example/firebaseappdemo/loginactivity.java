package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class loginactivity extends AppCompatActivity {

    EditText linemail,linpass;
    Button login;
    FirebaseAuth auth;
    ProgressBar pbar;
    FirebaseFirestore fstore;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        loginmethod();
        forgotpassword();
        TextView sup = findViewById(R.id.textView5);
        sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginactivity.this,signupactivity.class));
            }
        });
    }
    void loginmethod()
    {
        auth = FirebaseAuth.getInstance();
        linemail = findViewById(R.id.linetmail);
        linpass = findViewById(R.id.linetpass);
        login = findViewById(R.id.linbutton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog p = new ProgressDialog(loginactivity.this);
                p.setTitle("Logging in");
                p.setMessage("Please wait");
                p.show();
                String linemailstr = linemail.getText().toString();
                String linpassstr = linpass.getText().toString();
                auth.signInWithEmailAndPassword(linemailstr,linpassstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                            String devicetoken = FirebaseInstanceId.getInstance().getToken();

                            db = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).child("device_token");
                            db.setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(loginactivity.this,"User logged in",Toast.LENGTH_SHORT).show();



                                    Intent i = new Intent(loginactivity.this,Chatactivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    p.dismiss();
                                }
                            });


                        }
                        else
                        {
                           if ( task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(loginactivity.this,"Credentials wrong",Toast.LENGTH_SHORT).show();
                                p.dismiss();
                            }
                            else
                            {
                                Toast.makeText(loginactivity.this,"Error",Toast.LENGTH_SHORT).show();
                                p.dismiss();
                            }
                        }
                    }
                });
            }
        });



    }

    void forgotpassword()
    {

        TextView ff = findViewById(R.id.textView3);
        ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetemail = new EditText(v.getContext());


                AlertDialog .Builder alert = new AlertDialog.Builder(v.getContext()).setTitle("Reset password").setMessage("enter registerd email")
                        .setView(resetemail)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String  rmail = resetemail.getText().toString();
                                auth.sendPasswordResetEmail(rmail).addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(loginactivity.this,"Email sent ",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(loginactivity.this,"Errorrrr ",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog a = alert.create();
                a.show();


            }
        });
    }



}
