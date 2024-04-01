package edu.northeastern.brainrush;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private ImageView profilePicture;

    private TextView name;
    private TextView score;
    private TextView level;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicture = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        score = findViewById(R.id.profile_score);
        level = findViewById(R.id.profile_level);

        String url = "gs://brain-rush-db21a.appspot.com/cool.png";

        getFileFromFirebaseStorage(this, url);
    }

    public void setProfilePicture(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                // Permission denied
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // Handle the selected image URI (e.g., display it in an ImageView)
            Log.v("picture selected", String.valueOf(selectedImageUri));
            profilePicture.setImageURI(selectedImageUri);
            storeProfilePicture(selectedImageUri);
        }
    }

    public void storeProfilePicture(Uri uri){
        // Get an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference
        StorageReference storageRef = storage.getReference();
        // Create a reference to "profilePictures/userId.jpg"
        StorageReference profilePicRef = storageRef.child("profilePictures/nick.jpg");

        profilePicRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Use this download URL for the user's profile picture
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                });

    }

    public void getFileFromFirebaseStorage(Context context, String url){
        FirebaseApp.initializeApp(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(url);
        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri).into(profilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
