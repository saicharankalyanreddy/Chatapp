package com.example.firebaseappdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
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

public class Groupmessageadapter extends RecyclerView.Adapter<Groupmessageadapter.groupmessageviewholder> {

    MediaController mediaController;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private List<Groupmessages> messagelist;

    public Groupmessageadapter(List<Groupmessages> messagelist) {
        this.messagelist = messagelist;


    }


    class  groupmessageviewholder extends RecyclerView.ViewHolder{

        TextView sendermessage,recievermessage,sendertime,recievertime;
        TextView sendername;
        CircleImageView senderimg;

        ImageView si,ri;
        VideoView senderv,recvid;

        AudioSenseiPlayerView sendaud,recaud;

        public groupmessageviewholder(@NonNull View itemView) {
            super(itemView);

            sendermessage = itemView.findViewById(R.id.group_sender_message);

            si = itemView.findViewById(R.id.senderimagemessage);

            ri= itemView.findViewById(R.id.recieverimagemessage);

            recievermessage= itemView.findViewById(R.id.group_message_text);
            sendertime = itemView.findViewById(R.id.timesender);
            sendername = itemView.findViewById(R.id.sendername);
            senderimg = itemView.findViewById(R.id.recieverimg);
            recievertime = itemView.findViewById(R.id.timereciever);
            senderv = itemView.findViewById(R.id.sendervideomessage);
            recvid=itemView.findViewById(R.id.recievervideomessage);
            sendaud=itemView.findViewById(R.id.sendaudg);
            recaud=itemView.findViewById(R.id.recaudg);
        }

        public void setimage(String image) {

            Picasso.get().load(image).into(senderimg);
        }
    }
    @NonNull
    @Override
    public Groupmessageadapter.groupmessageviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupmessagelayout,parent,false);
        return new groupmessageviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Groupmessageadapter.groupmessageviewholder holder, final int position) {

        final Groupmessages c = messagelist.get(position);
        final String currentuserid = auth.getCurrentUser().getUid();

        final String senderuserid = c.getFrom();

        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("users").child(senderuserid);

        userref.keepSynced(true);



        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();


                String type = c.getType();

                holder.recievermessage.setVisibility(View.GONE);
                holder.sendertime.setVisibility(View.GONE);
                holder.sendermessage.setVisibility(View.GONE);
                holder.recievertime.setVisibility(View.GONE);
                holder.senderimg.setVisibility(View.GONE);
                holder.sendername.setVisibility(View.GONE);
                holder.ri.setVisibility(View.GONE);
                holder.si.setVisibility(View.GONE);
                holder.senderv.setVisibility(View.GONE);
                holder.recvid.setVisibility(View.GONE);
                holder.recaud.setVisibility(View.GONE);
                holder.sendaud.setVisibility(View.GONE);


                if(type.equals("text")){

                    if(senderuserid.equals(currentuserid)){

                        holder.sendermessage.setText(c.getMessage());
                        holder.sendertime.setText(c.getTime());
                        holder.sendermessage.setVisibility(View.VISIBLE);
                        holder.sendertime.setVisibility(View.VISIBLE);
                        holder.sendermessage.setTextColor(Color.BLACK);
                        holder.sendermessage.setBackgroundResource(R.drawable.senderbackground);

                    }
                    else {
                        holder.recievermessage.setText(c.getMessage());
                        holder.recievermessage.setVisibility(View.VISIBLE);
                        holder.sendername.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setText(username);
                        holder.setimage(image);
                        holder.recievertime.setVisibility(View.VISIBLE);
                        holder.recievermessage.setBackgroundResource(R.drawable.message_text_background);
                        holder.recievermessage.setTextColor(Color.WHITE);

                    }

                }

                else if(type.equals("image")){


                    if(senderuserid.equals(currentuserid)){
                        holder.si.setVisibility(View.VISIBLE);

                        Picasso.get().load(c.getMessage()).into(holder.si);
                    }
                    else {
                        holder.ri.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setText(username);
                        holder.setimage(image);
                        holder.sendername.setText(username);
                        holder.setimage(image);
                        holder.recievertime.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        Picasso.get().load(c.getMessage()).into(holder.ri);
                    }

                }

                else if (type.equals("pdf")){

                    if(senderuserid.equals(currentuserid)){
                        holder.si.setVisibility(View.VISIBLE);

                        holder.si.setBackgroundResource(R.drawable.pdfs);

                        final String mm = messagelist.get(position).getMessage();

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
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagelist.get(position).getMessage()));
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });



                    }
                    else {
                        holder.ri.setVisibility(View.VISIBLE);
                        holder.sendername.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setText(username);
                        holder.setimage(image);

                        final String mm = messagelist.get(position).getMessage();

                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.ri.setBackgroundResource(R.drawable.pdf);

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
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagelist.get(position).getMessage()));
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

                    if(senderuserid.equals(currentuserid)){
                        holder.senderv.setVisibility(View.VISIBLE);
                        final String mm = messagelist.get(position).getMessage();
                        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(mm, MediaStore.Images.Thumbnails.MINI_KIND);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.senderimg.setEnabled(false);
                        holder.senderimg.setImageBitmap(bmThumbnail);
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
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagelist.get(position).getMessage()));

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
                        holder.sendername.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setText(username);
                        holder.setimage(image);
                        final String mm = messagelist.get(position).getMessage();
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
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagelist.get(position).getMessage()));

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



                }

                else if(type.equals("audiorec")){

                    if(senderuserid.equals(currentuserid)){

                        holder.recaud.setVisibility(View.VISIBLE);
                        final String mm = messagelist.get(position).getMessage();
                        holder.recaud.setAudioTarget(mm);

                    }
                    else {
                        holder.sendaud.setVisibility(View.VISIBLE);
                        holder.sendername.setVisibility(View.VISIBLE);
                        holder.senderimg.setVisibility(View.VISIBLE);
                        holder.sendername.setText(username);
                        holder.setimage(image);
                        final String mm = messagelist.get(position).getMessage();
                        holder.sendaud.setAudioTarget(mm);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }
}
