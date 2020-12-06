package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import javax.sql.StatementEvent;

public class signupactivity extends AppCompatActivity {

    EditText em,uname,phone,pass,cpass;
    Button siup;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        signup();

    }

    void signup()
    {
        uname = findViewById(R.id.etuname);
        em = findViewById(R.id.etemail);
        phone = findViewById(R.id.etphone);
        pass = findViewById(R.id.etpass);
        cpass = findViewById(R.id.etcpass);
        siup = findViewById(R.id.sup);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        siup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String unamestr = uname.getText().toString().trim();
                        final String emailstr = em.getText().toString().trim();
                        final String phonestr = phone.getText().toString().trim();
                        final String passstr = pass.getText().toString().trim();
                        String cpassstr = cpass.getText().toString().trim();
                        if(!(passstr.equals(cpassstr)))
                        {
                            Toast.makeText(signupactivity.this,"password and confirm password do not watch",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(TextUtils.isEmpty(unamestr))
                            {
                                Toast.makeText(signupactivity.this,"Username can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            if(TextUtils.isEmpty(emailstr))
                            {
                                Toast.makeText(signupactivity.this,"Email can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            if(TextUtils.isEmpty(passstr))
                            {
                                Toast.makeText(signupactivity.this,"Password can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            if(TextUtils.isEmpty(phonestr))
                            {
                                Toast.makeText(signupactivity.this,"Phone number can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            if(TextUtils.isEmpty(cpassstr))
                            {
                                Toast.makeText(signupactivity.this,"Confirm your password that field can not be empty",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                final ProgressDialog p = new ProgressDialog(signupactivity.this);
                                p.setTitle("Signup");
                                p.setMessage("Please wait");
                                p.show();
                                auth.createUserWithEmailAndPassword(emailstr,passstr).addOnCompleteListener(
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful())
                                                {
                                                    String devicetoken = FirebaseInstanceId.getInstance().getToken();
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                                                    databaseReference.child("device_token").setValue(devicetoken);
                                                    FirebaseUser fuser = auth.getCurrentUser();
                                                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(signupactivity.this,"Email has sent for verification",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(signupactivity.this,"Error sending the email check your network connection",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    Toast.makeText(signupactivity.this,"Account created",Toast.LENGTH_SHORT).show();
                                                    final Intent intent = new Intent(signupactivity.this,mainpage.class);



                                                    String userid = auth.getCurrentUser().getUid();
                                                    //
                                                    DatabaseReference db = firebaseDatabase.getReference().child("users").child(userid);
                                                    //
                                                    DocumentReference documentReference = firestore.collection("users").document(userid);
                                                    CollectionReference d = documentReference.collection("Transactions");
                                                    Map<String,Object> user = new HashMap<>();
                                                    user.put("username",unamestr);
                                                    user.put("Email",emailstr);
                                                    user.put("Phonenumber",phonestr);
                                                    user.put("status","Default");
                                                    user.put("Image","default");

                                                    //
                                                    db.setValue(user);

                                                    //
                                                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(signupactivity.this,"Profile saved",Toast.LENGTH_SHORT).show();
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });

                                                }
                                                else {
                                                    p.dismiss();
                                                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                                    {
                                                        Toast.makeText(signupactivity.this,"Email already registered",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    Toast.makeText(signupactivity.this,"Error!!!!",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                );
                            }
                        }



                    }
                }
        );

    }
}
