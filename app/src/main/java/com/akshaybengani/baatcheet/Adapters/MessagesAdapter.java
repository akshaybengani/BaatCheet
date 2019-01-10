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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
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
    public void onBindViewHolder(@NonNull final MessagesAdapterViewHolder viewHolder, int i) {

        viewHolder.message.setText(messageModelList.get(i).getMessage());

        /*
        * Previously we have used imageViews in both the sides but the problem was
        * the imageURL is single and this holder sets the imageurl to both the sides
        *
        * Since we have ImageView in only one side not in both sides as such
        * we need to handle the set operation for the right side layout also
        * therefore to avoid the exception raised by Android I have used try catch
        * as such when the holder is in left then no exception occur and if it is in right
        * then exception will occur but we have dealed with that
        *
        * In Future try to search some better option for handling this situation
        */
        try{
            if (imageURL.equals("default")){
                viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.get().load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageURL).into(viewHolder.profileImage);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (i == messageModelList.size()-1){
            if (messageModelList.get(i).isIsseen()){
                viewHolder.seenStatus.setText("Seen");
            }
            else {
                viewHolder.seenStatus.setText("Delivered");
            }
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
        public TextView seenStatus;

        public MessagesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.recyMessage);
            profileImage = itemView.findViewById(R.id.recyProImage);
            seenStatus = itemView.findViewById(R.id.seenStatus);


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
