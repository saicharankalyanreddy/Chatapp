package com.example.firebaseappdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;




public class Messageadapter extends RecyclerView.Adapter<Messageadapter.messageviewholder> {







    FirebaseAuth auth = FirebaseAuth.getInstance();

    MediaController mediaController;


    private List<Messages> mmessagelist;

    Messageadapter(List<Messages> mmessagelist){
        this.mmessagelist = mmessagelist;
    }
    public class messageviewholder extends RecyclerView.ViewHolder{

        TextView messageText;
        CircleImageView profileimage;

        TextView sendermessage;

        ImageView recimg,sendimg;

        VideoView recvid,sendvid;

        AudioSenseiPlayerView dc,sc;


        public messageviewholder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_single_text);
            profileimage = itemView.findViewById(R.id.message_single_profile);
            recvid = itemView.findViewById(R.id.recvid);
            sendvid = itemView.findViewById(R.id.sendvid);
            dc = itemView.findViewById(R.id.recaud);
            sc=itemView.findViewById(R.id.sendaud);

            recimg= itemView.findViewById(R.id.recimg);

            sendimg = itemView.findViewById(R.id.sendimg);

            sendermessage = itemView.findViewById(R.id.sender_message);


        }

        public void setimage(String imageref) {

            Picasso.get().load(imageref).placeholder(R.drawable.man).into(profileimage);
        }
    }

    @NonNull
    @Override
    public messageviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new messageviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final messageviewholder holder, final int position) {

        Messages c = mmessagelist.get(position);


        String currentuserid = auth.getCurrentUser().getUid();

        String from_user_id = c.getFrom();

        DatabaseReference imgdb = FirebaseDatabase.getInstance().getReference().child("users").child(from_user_id);
        imgdb.keepSynced(true);



        imgdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageref =  dataSnapshot.child("Image").getValue().toString();

                holder.setimage(imageref);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String type = c.getType();


        holder.sendermessage.setVisibility(View.GONE);
        holder.sc.setVisibility(View.GONE);
        holder.sendvid.setVisibility(View.GONE);
        holder.recvid.setVisibility(View.GONE);
        holder.profileimage.setVisibility(View.GONE);
        holder.messageText.setVisibility(View.GONE);
        holder.sendimg.setVisibility(View.GONE);
        holder.recimg.setVisibility(View.GONE);
        holder.dc.setVisibility(View.GONE);

        if(type.equals("text")){


            if(from_user_id.equals(currentuserid)){

                holder.sendermessage.setText(c.getMessage());
             holder.sendermessage.setVisibility(View.VISIBLE);
                holder.sendermessage.setTextColor(Color.BLACK);
                holder.sendermessage.setBackgroundResource(R.drawable.senderbackground);
            }
            else {
                holder.messageText.setText(c.getMessage());
                holder.profileimage.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);

            }



        }

        else if(type.equals("image")){

            if(from_user_id.equals(currentuserid)){
                holder.sendimg.setVisibility(View.VISIBLE);

                Picasso.get().load(c.getMessage()).into(holder.sendimg);
            }
            else {
                holder.recimg.setVisibility(View.VISIBLE);

                holder.profileimage.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMessage()).into(holder.recimg);
            }

        }
        else if (type.equals("pdf")){

            if(from_user_id.equals(currentuserid)){
                holder.sendimg.setVisibility(View.VISIBLE);

                holder.sendimg.setBackgroundResource(R.drawable.pdfs);

                final String mm = mmessagelist.get(position).getMessage();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = {
                                "Open pdf",
                                "Download pdf"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("What do you want to do");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent i = new Intent(holder.itemView.getContext(),pdfview.class);
                                    i.putExtra("pdflink",mm);
                                    holder.itemView.getContext().startActivity(i);

                                }
                                if (which == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mmessagelist.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }
                });

            }
            else {
                holder.recimg.setVisibility(View.VISIBLE);

                final String mm = mmessagelist.get(position).getMessage();

                holder.profileimage.setVisibility(View.VISIBLE);
                holder.recimg.setBackgroundResource(R.drawable.pdf);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = {
                                "Open pdf",
                                "Download pdf"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("What do you want to do");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent i = new Intent(holder.itemView.getContext(),pdfview.class);
                                    i.putExtra("pdflink",mm);
                                    holder.itemView.getContext().startActivity(i);

                                }
                                if (which == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mmessagelist.get(position).getMessage()));

                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                    }
                });
            }



        }
        else if(type.equals("video")){
            if(from_user_id.equals(currentuserid) ){

                holder.sendvid.setVisibility(View.VISIBLE);





                final String mm = mmessagelist.get(position).getMessage();
                mediaController = new MediaController(holder.itemView.getContext());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = {
                                "Play video",
                                "Download Video"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("What do you want to do");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){

                                    Intent intent = new Intent(holder.itemView.getContext(),Videoview.class);
                                    intent.putExtra("videolink",mm);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                if (which == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mmessagelist.get(position).getMessage()));

                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                        //holder.sendvid.setVideoPath(mm);

                        //holder.sendvid.setMediaController(mediaController);
                        //mediaController.setAnchorView(holder.sendvid);
                       // holder.sendvid.start();

                    }
                });

            }

            else {
                holder.recvid.setVisibility(View.VISIBLE);
                final String mm = mmessagelist.get(position).getMessage();





                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        CharSequence options[] = {
                                "Play video",
                                "Download Video"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("What do you want to do");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){

                                    Intent intent = new Intent(holder.itemView.getContext(),Videoview.class);
                                    intent.putExtra("videolink",mm);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                if (which == 1){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mmessagelist.get(position).getMessage()));

                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();

                       // holder.recvid.setVideoPath(mm);
                        //holder.recvid.start();
                       // Intent intent = new Intent(holder.itemView.getContext(),Videoview.class);
                        //intent.putExtra("videolink",mm);
                        //holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }

        else if(type.equals("audiorec")){

            if(from_user_id.equals(currentuserid)){

                holder.dc.setVisibility(View.VISIBLE);
                final String mm = mmessagelist.get(position).getMessage();
                holder.dc.setAudioTarget(mm);

            }
            else {
                holder.sc.setVisibility(View.VISIBLE);
                final String mm = mmessagelist.get(position).getMessage();
                holder.sc.setAudioTarget(mm);

            }

        }
        

    }

    @Override
    public int getItemCount() {

        return mmessagelist.size();
    }



}
