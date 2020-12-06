package com.example.firebaseappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Allusersactivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    FirebaseRecyclerOptions<Users> options;
    DatabaseReference mUsersDatabase;


    DatabaseReference userref;

    SearchView sc;

    FirebaseAuth auth;
    Query q;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusersactivity);
        recyclerView = findViewById(R.id.userslist);

        auth = FirebaseAuth.getInstance();

        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        sc = findViewById(R.id.sv);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        sc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query fq = mUsersDatabase.orderByChild("username").startAt(query).endAt(query+"\uf8ff");
                search(fq);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query fq = mUsersDatabase.orderByChild("username").startAt(newText).endAt(newText+"\uf8ff");
                search(fq);
                return false;
            }
        });
    }

    void search(Query fq){
        auth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(fq, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

                        final String user_id = getRef(position).getKey();
                            holder.tv_all_users_username.setText(model.getUsername());
                            holder.alluserstatus.setText(model.getStatus());
                            holder.setImage(model.getImage());

                        holder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                    Intent allusersprofile = new Intent(Allusersactivity.this,Allusersprofileactivity.class);
                                    allusersprofile.putExtra("Userid",user_id);
                                    startActivity(allusersprofile);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout, viewGroup, false);
                        return new UsersViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    protected void onStart() {

        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
        final ProgressDialog p = new ProgressDialog(Allusersactivity.this);
        p.setTitle("Loading all users");
        p.setMessage("Please wait");
        p.show();
        search(mUsersDatabase);
        p.dismiss();


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mview;
        TextView tv_all_users_username ;//, tv_all_users_status;
        CircleImageView iv_all_users_image;
        TextView alluserstatus;

        UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;

            tv_all_users_username = itemView.findViewById(R.id.user_single_name);
            //tv_all_users_status = itemView.findViewById(R.id.tv_all_users_status);
            iv_all_users_image = itemView.findViewById(R.id.user_single_image);

            alluserstatus = itemView.findViewById(R.id.user_single_status);


        }

        public void setImage(String image) {

            Picasso.get().load(image).placeholder(R.drawable.man).into(iv_all_users_image);
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }
}

