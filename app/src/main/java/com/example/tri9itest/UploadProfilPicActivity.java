package com.example.tri9itest;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class UploadProfilPicActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfil;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profil_pic);

        getSupportActionBar().setTitle("Upload profil picture");

        authProfil = FirebaseAuth.getInstance();
        firebaseUser = authProfil.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_btn);
        Button buttonUploadPic = findViewById(R.id.upload_pic_btn);
        imageViewUploadPic = findViewById(R.id.imgv_profil_dp);

        //uniform resource identifier
        Uri uri = firebaseUser.getPhotoUrl();
        /*Picasso.with(UploadProfilPicActivity.this).load(uri).into(imageViewUploadPic);*/

        //choose image to upload
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        //Upload picture
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPic();
            }
        });
    }

    private void UploadPic() {
        if(uriImage != null){
            //save img wit id ta3 user
            StorageReference fileReference = storageReference.child(authProfil.getCurrentUser().getUid() + "/DisplayPics."+ getFileExtention(uriImage));

            //upload pic into storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfil.getCurrentUser();

                            //set the display img of user
                            UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                        }
                    });
                    Intent intent = new Intent(UploadProfilPicActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private String getFileExtention(Uri uriImage) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode , @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_add_trip) {
            Intent intent = new Intent(UploadProfilPicActivity.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(UploadProfilPicActivity.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(UploadProfilPicActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(UploadProfilPicActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.menu_logout) {
            authProfil.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadProfilPicActivity.this , MainActivity.class);

            //clear stack to prevent user coming back to profil after loggin out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


        return  super.onOptionsItemSelected(item);
    }
}