package com.akshaybengani.baatcheet.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akshaybengani.baatcheet.ModelClasses.UserModel;
import com.akshaybengani.baatcheet.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView circleImageView;
    private TextView textViewUserName;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    private ProgressDialog progressDialog;
    private StorageReference firebaseStorage;
    private final static int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String downloadURL="";
    private Bitmap bitmap=null;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageView = view.findViewById(R.id.profilePicture);
        textViewUserName = view.findViewById(R.id.profileName);
        progressBar  = view.findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading ....");
        progressDialog.setMessage("Adding your image to your profile.");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseStorage = FirebaseStorage.getInstance().getReference("BaatCheet/uploads/");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Users/").child(firebaseUser.getUid());

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                textViewUserName.setText(userModel.getUsername());
                if (userModel.getImageURL().equals("default")) {
                    Picasso.get().load(R.mipmap.ic_launcher).into(circleImageView);
                    circleImageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Picasso.get().load(userModel.getImageURL()).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(userModel.getImageURL()).into(circleImageView);
                        }
                    });
                    circleImageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }// end of onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });// end of value event listener

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showImageChooser();
            }
        });

        return view;
    }// end of onCreate

        private void uploadImage() {

            progressDialog.show();
            String fileExtension = getMyFileExtension();
            final StorageReference storageReference = firebaseStorage.child(System.currentTimeMillis()+"."+fileExtension);

            byte [] compImage = getCompressedImage(fileExtension);

            storageReference.putBytes(compImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadURL = taskSnapshot.getDownloadUrl().toString();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("imageURL",downloadURL);
                    databaseReference.updateChildren(hashMap);
                    progressDialog.dismiss();
                }
            })

         .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Unable to handle your request right now please try again",Toast.LENGTH_SHORT).show();
                    Log.d("ImageUpdateError",e.getMessage());
                    progressDialog.dismiss();
                }
            });

        } // end of uploadImage

    private byte[] getCompressedImage(String fileExtension) {

        //bitmap = BitmapFactory.decodeFile(imageUri.toString());
        ByteArrayOutputStream bstrem = new ByteArrayOutputStream();

        if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg") ){
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,bstrem);
        }
        if (fileExtension.equalsIgnoreCase("png")){
            bitmap.compress(Bitmap.CompressFormat.PNG,20,bstrem);
        }
        return bstrem.toByteArray();
    }

    /* This method is used to get the extension of the image,
         since the image extension can be png or jpg or anything else
         therefore we need a content resolver to identify its extension
     */
    private String getMyFileExtension() {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() != null){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage();
        }
    }

}// end  of class
