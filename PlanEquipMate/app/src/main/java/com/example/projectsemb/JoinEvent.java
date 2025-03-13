package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class JoinEvent extends AppCompatActivity {

    TextInputEditText editTextEventId;
    MaterialButton btnJoinEvent;
    BottomNavigationView bottomNav;
    TextInputLayout layoutEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        InitViews();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNav.setSelectedItemId(R.id.toJoin);
        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.toHome:
                    Intent homeEventIntent = new Intent(JoinEvent.this, HomeActivity.class);
                    startActivity(homeEventIntent);
                    break;
                case R.id.toAdd:
                    Intent addEventIntent = new Intent(JoinEvent.this, AddEventActivity.class);
                    startActivity(addEventIntent);
                    break;
                case R.id.toJoin:
                    // Do nothing since we're already in the JoinEventActivity
                    break;
            }
            return true;
        });

        btnJoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = editTextEventId.getText().toString();
                String userId = currentUser.getUid();

                if (!eventId.isEmpty()) {
                    joinEvent(eventId, userId);
                } else {
                    layoutEventId.setError("Empty input");
                }
            }
        });
    }

    private void joinEvent(String eventId, String userId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        List<String> participantIds = event.getParticipantIds();
                        // Check if the participant is already signed in to the event
                        if (participantIds.contains(userId)) {
                            // Participant is already signed in, show a toast message
                            Toast.makeText(getApplicationContext(), "You are already signed in to this event", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            participantIds.add(userId);
                            event.setParticipantIds(participantIds);
                        }
                        // Update the event with the modified participantIds
                        eventRef.setValue(event)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Update successful
                                        Toast.makeText(JoinEvent.this, "Joined the event successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(JoinEvent.this, HomeActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Update failed
                                        Toast.makeText(JoinEvent.this, "Failed to join the event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(JoinEvent.this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JoinEvent.this, "Event not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(JoinEvent.this, "Failed to join the event: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitViews() {
        editTextEventId = findViewById(R.id.editTextEventId);
        btnJoinEvent = findViewById(R.id.btnJoinEvent);
        bottomNav = findViewById(R.id.bottomNav);
        layoutEventId = findViewById(R.id.layoutEventId);
    }
}