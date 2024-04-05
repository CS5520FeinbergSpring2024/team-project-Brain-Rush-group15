package edu.northeastern.brainrush.match;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.northeastern.brainrush.R;

public class MatchingDemo extends AppCompatActivity {

    EditText userName;
    TextView roomView;
    DatabaseReference myRef;
    boolean matching;

    String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_test);

        matching = false;
        myRef = FirebaseDatabase.getInstance().getReference().child("MatchPool");
        userName = findViewById(R.id.username);
        roomView = findViewById(R.id.room_id);
        roomId = null;
    }

    public void matchClicked(View v){
        if ( matching){
            Toast.makeText(this, "is already matching", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentName = userName.getText().toString();
        if(currentName.equals("")){return;}
        // check if have host
        matching = true;
        myRef.child("Host").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // yes, find host, transactional change, create room
                    String hostName = null;
                    for(DataSnapshot host: snapshot.getChildren()){
                        if(Objects.equals((String)host.child("status").getValue(), "Waiting")) {
                            hostName = host.getKey();
                            break;
                        }
                    }

                    if(hostName == null){
                        // enter queue
                        addHost(currentName);
                        return;
                    }
                    String finalHostName = hostName;
                    myRef.child("Host").child(finalHostName).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            if(mutableData == null){
                                addHost(currentName);
                                return Transaction.abort();
                            }

                            MatchRoom matchingRoom = new MatchRoom(finalHostName, currentName);
                            String newId = finalHostName + "_" + currentName;
                            myRef.child("Match").child(newId).setValue(matchingRoom);
                            mutableData.child("status").setValue(newId);
                            roomId = newId;
                            myRef.child("Match").child(newId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Check if data exists
                                    if (!dataSnapshot.exists()) {
                                        exitClicked(null);
                                        Log.d("Guest", "Data has been removed");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle errors
                                    Log.d("TAG", "Error: " + databaseError.getMessage());
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomView.setText(String.format("RoomId: %s", roomId));
                                }
                            });

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            Log.v("trans", "Transaction completed");
                        }
                    });


                } else {
                    // no, add to host, listen to change
                    addHost(currentName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("Check host list", "failed");
            }
        });
    }

    public void addHost(String key){
        Host host = new Host();
        myRef.child("Host").child(key).setValue(host, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Write was successful!
                    matching = true;
                    myRef.child("Host").child(key).child("status").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String status = (String) snapshot.getValue();
                                if(status == null || status.equals("Waiting")){return;}
                                roomId = snapshot.getKey();
                                myRef.child("Match").child(roomId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Check if data exists
                                        if (!dataSnapshot.exists()) {
                                            exitClicked(null);
                                            Log.d("Host", "Data has been removed");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors
                                        Log.d("TAG", "Error: " + databaseError.getMessage());
                                    }
                                });
                                myRef.child("Host").child(key).removeValue();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update UI here
                                        roomView.setText(String.format("RoomId: %s", roomId));
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    public void exitClicked(View v){
        if(!matching){
            Toast.makeText(this, "is not in a match", Toast.LENGTH_SHORT).show();
        }
        if(roomId == null){
            String currentName = userName.getText().toString();
            myRef.child("Host").child(currentName).removeValue();
        }
        else {
            myRef.child("Match").child(roomId).removeValue();
            roomId = null;
        }
        matching = false;
        roomView.setText("RoomId: None");
    }
}
