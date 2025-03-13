package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.CharArrayWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class EventDisplay extends AppCompatActivity {

    TextView textEventID, textEventParticipants, textEventName, textEventLocation,
            textEventDate, textEventTime, textEventDescription, textEquipmentList;
    ImageView clipboardButton, deleteEvent;
    RecyclerView recyclerViewParticipants;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);

        textEventID = findViewById(R.id.textEventID);
        textEventName = findViewById(R.id.textEventName);
        textEventLocation = findViewById(R.id.textEventLocation);
        textEventDate = findViewById(R.id.textEventDate);
        textEventTime = findViewById(R.id.textEventTime);
        textEventDescription = findViewById(R.id.textEventDescription);
        textEventParticipants = findViewById(R.id.textEventParticipants);
        clipboardButton = findViewById(R.id.clipboardButton);
        deleteEvent = findViewById(R.id.deleteEvent);
        textEquipmentList = findViewById(R.id.textEquipmentList);

        recyclerViewParticipants = findViewById(R.id.recyclerViewParticipants);
        recyclerViewParticipants.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        // Retrieve the event details from intent
        Intent intent = getIntent();
        if (intent != null) {
            String eventId = intent.getStringExtra("eventId");
            String eventName = intent.getStringExtra("eventName");
            String eventLocation = intent.getStringExtra("eventLocation");
            String eventDate = intent.getStringExtra("eventDate");
            String eventTime = intent.getStringExtra("eventTime");
            String eventDescription = intent.getStringExtra("eventDescription");


            // Set the event details in the views
            //textEventID.setText(eventId);
            textEventName.setText(eventName);
            textEventLocation.setText(eventLocation);
            textEventDate.setText(eventDate);
            textEventTime.setText(eventTime);
            textEventDescription.setText(eventDescription);

            DatabaseReference eventRef = database.getReference("events").child(eventId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Event event = dataSnapshot.getValue(Event.class);
                        if (event != null) {
                            List<String> participantIds = event.getParticipantIds();

                            DatabaseReference usersRef = database.getReference("Users");
                            StringBuilder namesBuilder = new StringBuilder();

                            for (String participantId : participantIds) {
                                usersRef.child(participantId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String fullName = dataSnapshot.child("fullName").getValue(String.class);
                                            // Append the full name to the StringBuilder
                                            namesBuilder.append(fullName).append("\n");
                                            // Set the accumulated full names in the TextView
                                            //textEventParticipants.setText(namesBuilder.toString());

                                            // Create the participant card adapter
                                            List<String> participantNames = Arrays.asList(namesBuilder.toString().split("\n"));
                                            ParticipantCardAdapter participantCardAdapter = new ParticipantCardAdapter(participantNames);
                                            String participantsText = "Participants: ";
                                            TextView textEventParticipants = findViewById(R.id.textEventParticipants);
                                            textEventParticipants.setText(participantsText + participantCardAdapter.getItemCount());
                                            recyclerViewParticipants.setAdapter(participantCardAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle the error, if any
                                        Toast.makeText(EventDisplay.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error, if any
                    Toast.makeText(EventDisplay.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

            deleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEvent(eventId);
                }
            });

            // Set click listener for the copy ImageView
            clipboardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyEventIDToClipboard(eventId);
                }
            });

            textEquipmentList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventDisplay.this, EquipmentListActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                }
            });
        }
    }

    private void copyEventIDToClipboard(String eventId) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("eventId", eventId);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Event ID copied", Toast.LENGTH_SHORT).show();
    }

    private void deleteEvent(String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDisplay.this);
        builder.setTitle("Delete Event")
                .setMessage("Are you sure you want to remove this event?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);

                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Event event = dataSnapshot.getValue(Event.class);

                                    if (event != null) {
                                        List<String> participantIds = event.getParticipantIds();
                                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        if (participantIds != null && participantIds.contains(userId)) {
                                            participantIds.remove(userId);

                                            if (participantIds.isEmpty()) {
                                                eventRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EventDisplay.this, "Event deleted", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(EventDisplay.this, HomeActivity.class));
                                                        } else {
                                                            Toast.makeText(EventDisplay.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                eventRef.child("participantIds").setValue(participantIds)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(EventDisplay.this, "Removed from event participants", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(EventDisplay.this, HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(EventDisplay.this, "Failed to remove from event participants", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(EventDisplay.this, "You are not a participant in this event", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle the error, if any
                                Toast.makeText(EventDisplay.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the dialog and do nothing
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public class ParticipantCardAdapter extends RecyclerView.Adapter<ParticipantCardAdapter.CardViewHolder> {
        private List<String> participantNames;

        public ParticipantCardAdapter(List<String> participantNames) {
            this.participantNames = participantNames;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant_card, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            String participantName = participantNames.get(position);
            holder.textParticipantName.setText(participantName);
        }

        @Override
        public int getItemCount() {
            return participantNames.size();
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            TextView textParticipantName;

            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                textParticipantName = itemView.findViewById(R.id.textParticipantName);
            }
        }
    }
}