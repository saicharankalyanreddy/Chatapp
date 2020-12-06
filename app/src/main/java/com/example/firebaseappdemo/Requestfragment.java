package com.example.firebaseappdemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Requestfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Requestfragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Requestfragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Requestfragment newInstance(String param1, String param2) {
        Requestfragment fragment = new Requestfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView reqlist;

    FirebaseAuth auth;
    FirebaseUser cuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    DatabaseReference reqref,userref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View reqview =inflater.inflate(R.layout.fragment_requestfragment, container, false);

        auth = FirebaseAuth.getInstance();

        Allusersprofileactivity allusersprofileactivity;

        cuser = auth.getCurrentUser();

        reqlist = reqview.findViewById(R.id.requestlist);
        reqlist.setLayoutManager(new LinearLayoutManager(getContext()));
        reqref = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        allusersprofileactivity = new Allusersprofileactivity();

        userref = FirebaseDatabase.getInstance().getReference().child("users");



        return reqview;





    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(reqref.child(cuser.getUid()),Requests.class)
                .build();

        FirebaseRecyclerAdapter<Requests,requestviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, requestviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final requestviewholder holder, int position, @NonNull Requests model) {

                final String luid = getRef(position).getKey();

                String reqtype = model.getReq_type();
                 DatabaseReference gettyperef = getRef(position).child("req_type").getRef();

                 gettyperef.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists()){
                             String type = dataSnapshot.getValue().toString();

                             if(type.equals("received")){


                                 userref.child(luid).addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         String uname = dataSnapshot.child("username").getValue().toString();
                                         String  uimg = dataSnapshot.child("Image").getValue().toString();
                                         holder.tv_req_users_username.setText(uname);
                                         Picasso.get().load(uimg).placeholder(R.drawable.man).into(holder.iv_req_users_image);

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });

                                 holder.accept.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         final String ctime = DateFormat.getDateTimeInstance().format(new Date());
                                         final DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();

                                         rootref.child("Friends").child(cuser.getUid()).child(luid).child("date").setValue(ctime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 rootref.child("Friends").child(luid).child(cuser.getUid()).child("date").setValue(ctime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         rootref.child("Friend_req").child(cuser.getUid()).child(luid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                             @Override
                                                             public void onSuccess(Void aVoid) {
                                                                 rootref.child("Friend_req").child(luid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                     @Override
                                                                     public void onSuccess(Void aVoid) {

                                                                         Allusersprofileactivity allusersprofileactivity = new Allusersprofileactivity();

                                                                         allusersprofileactivity.current_state = "friends";

                                                                     }
                                                                 });
                                                             }
                                                         });
                                                     }
                                                 });
                                             }
                                         });




                                     }
                                 });

                                 holder.reject.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         final DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();

                                         rootref.child("Friend_req").child(cuser.getUid()).child(luid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 rootref.child("Friend_req").child(luid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {

                                                         Allusersprofileactivity allusersprofileactivity = new Allusersprofileactivity();
                                                         allusersprofileactivity.current_state = "not_friends";


                                                         Toast.makeText(holder.itemView.getContext(),"Cancelled friend request",Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                             }
                                         });
                                     }
                                 });

                             }
                             else if(type.equals("sent")){
                                 holder.reject.setText("Cancel Friend Request");
                                 holder.accept.setVisibility(View.GONE);
                                 holder.accept.setEnabled(false);

                                 userref.child(luid).addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         String uname = dataSnapshot.child("username").getValue().toString();
                                         String  uimg = dataSnapshot.child("Image").getValue().toString();
                                         holder.tv_req_users_username.setText(uname);
                                         Picasso.get().load(uimg).placeholder(R.drawable.man).into(holder.iv_req_users_image);

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });

                                 holder.reject.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         final DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();

                                         rootref.child("Friend_req").child(cuser.getUid()).child(luid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 rootref.child("Friend_req").child(luid).child(cuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {

                                                         Allusersprofileactivity allusersprofileactivity = new Allusersprofileactivity();
                                                         allusersprofileactivity.current_state = "not_friends";


                                                         Toast.makeText(holder.itemView.getContext(),"Cancelled friend request",Toast.LENGTH_SHORT).show();
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
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });



            }

            @NonNull
            @Override
            public requestviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_single,parent,false);
                return new Requestfragment.requestviewholder(v);
            }
        };

        reqlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }


    public  class requestviewholder extends RecyclerView.ViewHolder{

        View mview;
        TextView tv_req_users_username ;//, tv_all_users_status;
        CircleImageView iv_req_users_image;
        Button accept,reject;

        public requestviewholder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            tv_req_users_username = itemView.findViewById(R.id.req_single_name);
            iv_req_users_image = itemView.findViewById(R.id.req_single_image);

            accept = itemView.findViewById(R.id.acceptreq);

            reject = itemView.findViewById(R.id.rejreq);



        }
    }


}
