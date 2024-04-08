package edu.northeastern.brainrush;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.brainrush.model.User;
import edu.northeastern.brainrush.BuildConfig;
import okhttp3.OkHttpClient;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_PERMISSION_REQUEST_CODE = 101;
    private static final int TAKE_PHOTO_PERMISSION_REQUEST_CODE = 102;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private ImageView profilePicture;

    private TextView name;
    private TextView score;
    private TextView level;
    private TextView mottoText;
    private String picturePathString;
    private StorageReference profilePicRef;
    private String chatGPTApiKey;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Gettting the Api key
        chatGPTApiKey = BuildConfig.ChatGPT_API_KEY;
        Log.v("key", chatGPTApiKey);

        profilePicture = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        score = findViewById(R.id.profile_score);
        level = findViewById(R.id.profile_level);
        mottoText = findViewById(R.id.mottoText);

        //Change later after get intent from previous page
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Setting up the date selected from the previous page
            user = (User) extras.get("user");
        }
        String userName = user.getName();
        name.setText(userName);
        level.setText("level: " + user.getExperience());
        score.setText("Score: " + user.getScore());



        picturePathString = "profilePictures/" + userName +".jpg";

        //Initialize database reference
        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        profilePicRef = storageRef.child(picturePathString);

        getUserPictureFromFirebaseStorage();
    }

    public void generateMotto(View view){
        String url = "https://api.openai.com/v1/chat/completions"; // Example URL

        RequestQueue requestQueue = Volley.newRequestQueue(this); // 'this' is Context
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            JSONArray content = new JSONArray();
            content.put(new JSONObject().put("type", "text").put("text", "generate a random motto?"));
            message.put("content", content);
            messages.put(message);
            jsonBody.put("messages", messages);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        try {
                            // Navigate through the JSON structure to get to the "choices" array
                            JSONArray choicesArray = response.getJSONArray("choices");
                            // Assuming you want the first item in "choices" array
                            if (choicesArray.length() > 0) {
                                JSONObject firstChoice = choicesArray.getJSONObject(0);
                                // Get the "message" object
                                JSONObject messageObject = firstChoice.getJSONObject("message");
                                // Extract the "content" string
                                String content = messageObject.getString("content");

                                // Print the content
                                mottoText.setText(content);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + chatGPTApiKey);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
        Log.v("result", String.valueOf(jsonObjectRequest));

    }

    public void backButtonClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void checkPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_PERMISSION_REQUEST_CODE);
        }
        else {
            takePhoto();
        }
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PICK_IMAGE_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted
            pickImage();
        }
    }

    public void setProfilePicture(View view){
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")) {
                    checkPermissionAndTakePhoto();
                } else if (options[which].equals("Choose from Gallery")) {
                    checkPermissionAndPickImage();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                pickImage();
            }
        }
        else if (requestCode == TAKE_PHOTO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    private void pickImage() {
        Intent pickImageIntent = new Intent();
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickImageIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void setDefaultPicture(){
        profilePicture.setImageResource(R.drawable.default_user);
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_user);
        storeProfilePicture(bm);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            Log.v("picture selected", String.valueOf(selectedImageUri));
            profilePicture.setImageURI(selectedImageUri);
            storeProfilePicture(selectedImageUri);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePicture.setImageBitmap(imageBitmap);
            storeProfilePicture(imageBitmap);
        }
    }

    public void storeProfilePicture(@NonNull Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        profilePicRef.putBytes(byteArray)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Use this download URL for the user's profile picture
                        Log.v("Success get picture", String.valueOf(downloadUri));
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                });
    }

    public void storeProfilePicture(Uri uri){
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

    public void getUserPictureFromFirebaseStorage(){
        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri).into(profilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                setDefaultPicture();
            }
        });
    }
}
