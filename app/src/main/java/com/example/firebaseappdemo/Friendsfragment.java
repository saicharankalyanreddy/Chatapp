package com.example.firebaseappdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Friendsfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Friendsfragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Friendsfragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Friendsfragment newInstance(String param1, String param2) {
        Friendsfragment fragment = new Friendsfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View friendsview;
    RecyclerView friendlist;

    DatabaseReference friendref;

    FirebaseAuth mauth;
    String cuserid;

    DatabaseReference userref;

    String userimager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        friendsview = inflater.inflate(R.layout.fragment_friendsfragment, container, false);

        friendlist = (RecyclerView) friendsview.findViewById(R.id.friendsrv);


        friendlist.setHasFixedSize(true);
        friendlist.setLayoutManager(new LinearLayoutManager(getContext()));


        mauth = FirebaseAuth.getInstance();
        cuserid = mauth.getUid();
        friendref = FirebaseDatabase.getInstance().getReference().child("Friends").child(cuserid);
        friendref.keepSynced(true);
        userref = FirebaseDatabase.getInstance().getReference().child("users");
        userref.keepSynced(true);


        return friendsview;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendref, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends,Friendsviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, Friendsviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Friendsviewholder holder, int position, @NonNull Friends model) {
                holder.date.setText(model.getDate());

                final String list_uid = getRef(position).getKey();
                userref.child(list_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        String userimage = dataSnapshot.child("Image").getValue().toString();
                        userimager = userimage;
                        if(dataSnapshot.hasChild("online")) {
                            String Online =  dataSnapshot.child("online").getValue().toString();
                            holder.usernameid.setText(username);
                            holder.setimage(userimage);
                                holder.setOnlineimage(Online);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Open profile","Chat"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("What do you wanna do??");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which ==0 ){

                                    Intent allusersprofile = new Intent(getContext(),Allusersprofileactivity.class);
                                    allusersprofile.putExtra("Userid",list_uid);
                                    startActivity(allusersprofile);
                                }
                                if(which == 1){


                                    Intent chatintent = new Intent(getContext(),ChatsingleActivity.class);
                                    chatintent.putExtra("Userid",list_uid);
                                    chatintent.putExtra("Userimage",userimager);

                                    startActivity(chatintent);


                                }

                            }
                        });
                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public Friendsviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout, viewGroup, false);
                return new Friendsfragment.Friendsviewholder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        friendlist.setAdapter(firebaseRecyclerAdapter);
    }

    public class Friendsviewholder extends RecyclerView.ViewHolder{

        View mview;
        TextView date,usernameid;
        CircleImageView image_f;
        ImageView onlineicon;

        public Friendsviewholder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            date = itemView.findViewById(R.id.user_single_status);
            usernameid = itemView.findViewById(R.id.user_single_name);
            onlineicon = itemView.findViewById(R.id.onlineicon);


        }

        public void setimage(String image) {
            image_f = itemView.findViewById(R.id.user_single_image);
                Picasso.get().load(image).placeholder(R.drawable.man).into(image_f);

        }

        public void setOnlineimage(String online) {

            if(online.equals("true"))
            {
                onlineicon.setVisibility(View.VISIBLE);
            }
            else {
                onlineicon.setVisibility(View.INVISIBLE);
            }


        }
    }


}
