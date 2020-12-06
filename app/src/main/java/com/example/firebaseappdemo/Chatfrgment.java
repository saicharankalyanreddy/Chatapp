package com.example.firebaseappdemo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import javax.sql.StatementEvent;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatfrgment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Chatfrgment() {
        // Required empty public constructor
    }
    public static Chatfrgment newInstance(String param1, String param2) {
        Chatfrgment fragment = new Chatfrgment();
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

    View chatview;

    RecyclerView chatlist;

    FirebaseAuth mauth;

    DatabaseReference userref;


    DatabaseReference chatref;
    String cusserid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatview =inflater.inflate(R.layout.fragment_chatfrgment, container, false);

        chatlist = chatview.findViewById(R.id.chat_list);
        chatlist.setHasFixedSize(true);
        chatlist.setLayoutManager(new LinearLayoutManager(getContext()));

        mauth = FirebaseAuth.getInstance();
        cusserid = mauth.getCurrentUser().getUid().toString();

        chatref = FirebaseDatabase.getInstance().getReference().child("chat").child(cusserid);

        userref = FirebaseDatabase.getInstance().getReference().child("users");






        return  chatview;


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Chats> options = new FirebaseRecyclerOptions.Builder<Chats>().setQuery(chatref,Chats.class).build();

        FirebaseRecyclerAdapter<Chats,chatviewholder> chatschatviewholderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, chatviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatviewholder holder, int position, @NonNull Chats model) {

                final String list_uid = getRef(position).getKey();

                holder.singles.setVisibility(View.GONE);

                userref.child(String.valueOf(list_uid)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String username = dataSnapshot.child("username").getValue().toString();
                        final String img = dataSnapshot.child("Image").getValue().toString();
                        holder.usernamec.setText(username);
                        holder.setimage(img);

                        holder.mviewc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatintent = new Intent(getContext(),ChatsingleActivity.class);
                                chatintent.putExtra("Userid",list_uid);
                                chatintent.putExtra("Userimage",img);

                                startActivity(chatintent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public chatviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new Chatfrgment.chatviewholder(view);
            }
        };

        chatschatviewholderFirebaseRecyclerAdapter.startListening();
        chatlist.setAdapter(chatschatviewholderFirebaseRecyclerAdapter);
    }

    class  chatviewholder extends RecyclerView.ViewHolder{
        TextView usernamec;
        CircleImageView cimg;

        TextView singles;

        View mviewc;


        public chatviewholder(@NonNull View itemView) {
            super(itemView);

            mviewc = itemView;
            singles = itemView.findViewById(R.id.user_single_status);
            usernamec = itemView.findViewById(R.id.user_single_name);

        }

        public void setimage(String image) {
            cimg = itemView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).placeholder(R.drawable.man).into(cimg);

        }
    }
}
