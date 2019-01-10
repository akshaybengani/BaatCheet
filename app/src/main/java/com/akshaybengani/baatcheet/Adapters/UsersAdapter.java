package com.akshaybengani.baatcheet.Adapters;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akshaybengani.baatcheet.MessageActivity;
import com.akshaybengani.baatcheet.ModelClasses.UserModel;
import com.akshaybengani.baatcheet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// create inner view holder class before extending
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersAdapterViewHolder> {

    private List<UserModel> userModelList;
    private boolean isChat;

    public UsersAdapter(List<UserModel> userModelList,boolean isChat) {
        this.userModelList = userModelList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UsersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.useritem,null);
        UsersAdapterViewHolder usersAdapterViewHolder = new UsersAdapterViewHolder(view);
        return usersAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersAdapterViewHolder holder, final int i) {

        holder.textViewProfileName.setText(userModelList.get(i).getUsername());

        if (userModelList.get(i).getImageURL().equals("default")){
            holder.circleImageViewProfileImage.setImageResource(R.mipmap.ic_launcher);
        }
        else {
         //   Picasso.get().load(userModelList.get(i).getImageURL()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.circleImageViewProfileImage, new Callback() {
         //       @Override
         //       public void onSuccess() {
         //       }
          //      @Override
           //     public void onError(Exception e) {
                    Picasso.get().load(userModelList.get(i).getImageURL()).into(holder.circleImageViewProfileImage);
          //      }
          //  });
        }

        if (isChat){
            if (userModelList.get(i).getStatus().equals("online")){
                holder.circleImageViewStatus.setImageResource(R.color.statusOnline);
            }else{
                holder.circleImageViewStatus.setImageResource(R.color.statusOffline);
            }
        }else {
            holder.circleImageViewStatus.setImageResource(R.color.statusOffline);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),MessageActivity.class);
                intent.putExtra("userId",userModelList.get(i).getId());
                intent.putExtra("imageURL",userModelList.get(i).getImageURL());
                intent.putExtra("userName",userModelList.get(i).getUsername());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public void filterList(ArrayList<UserModel> userFilteredList) {
        userModelList = userFilteredList;
        notifyDataSetChanged();
    }


    public static class UsersAdapterViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageViewProfileImage,circleImageViewStatus;
        TextView textViewProfileName;

        public UsersAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

             circleImageViewProfileImage = itemView.findViewById(R.id.recyProImage);
             textViewProfileName = itemView.findViewById(R.id.recyProName);
            circleImageViewStatus = itemView.findViewById(R.id.userStatus);
        }


    }// end of class




}// end of class
