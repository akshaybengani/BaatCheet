package com.akshaybengani.baatcheet.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshaybengani.baatcheet.ModelClasses.MessageModel;
import com.akshaybengani.baatcheet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesAdapterViewHolder>{

    private List<MessageModel> messageModelList;
    private String imageURL;

    public  static final  int MSG_TYPE_LEFT = 0;
    public  static final  int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    public MessagesAdapter(List<MessageModel> messageModelList,String imageURL) {
        this.messageModelList = messageModelList;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessagesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_right,null);
            MessagesAdapter.MessagesAdapterViewHolder messagesAdapterViewHolder = new MessagesAdapter.MessagesAdapterViewHolder(view);
            return messagesAdapterViewHolder;
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_left,null);
            MessagesAdapter.MessagesAdapterViewHolder messagesAdapterViewHolder = new MessagesAdapter.MessagesAdapterViewHolder(view);
            return messagesAdapterViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapterViewHolder viewHolder, int i) {

        viewHolder.message.setText(messageModelList.get(i).getMessage());

        if (imageURL.equals("default")){
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(imageURL).into(viewHolder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    // start of inner class
    public  static  class MessagesAdapterViewHolder extends RecyclerView.ViewHolder{

        public TextView message;
        public CircleImageView profileImage;

        public MessagesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.recyMessage);
            profileImage = itemView.findViewById(R.id.recyProImage);


        }// end of constructor



    }// end of inner class

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();
        if (messageModelList.get(position).getSender().equals(userID)){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }


}// end of class
