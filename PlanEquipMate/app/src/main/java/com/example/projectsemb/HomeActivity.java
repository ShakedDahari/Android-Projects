package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.j256.ormlite.stmt.query.In;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    RecyclerView eventRecyclerView;
    EventAdapter eventAdapter;
    List<Event> eventList;
    DatabaseReference eventsRef;
    MaterialCardView eventCardView;
    BottomNavigationView bottom_navigation;
    TextView noEventsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        InitViews();

        bottom_navigation.setSelectedItemId(R.id.toHome);
        bottom_navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.toHome:
                    // Do nothing since we're already in the HomeActivity
                    break;
                case R.id.toAdd:
                    Intent addEventIntent = new Intent(HomeActivity.this, AddEventActivity.class);
                    startActivity(addEventIntent);
                    break;
                case R.id.toJoin:
                    Intent joinEventIntent = new Intent(HomeActivity.this, JoinEvent.class);
                    startActivity(joinEventIntent);
                    break;
            }
             return true;
        });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();  // Replace this with your actual event list
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing list
                eventList.clear();

                // Get the current date and time
                Calendar currentDateTime = Calendar.getInstance();

                String userId = currentUser.getUid();
                //Toast.makeText(HomeActivity.this, userId, Toast.LENGTH_SHORT).show();

                // Iterate over the snapshots to extract events
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    String eventDate = event.getDate();
                    String eventTime = event.getTime();

                    // Convert the event date and time to Calendar objects
                    Calendar eventDateTime = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    try {
                        Date eventDateTimeObj = dateFormat.parse(eventDate + " " + eventTime);
                        eventDateTime.setTime(eventDateTimeObj);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Compare the event date and time with the current date and time
                    if (eventDateTime.before(currentDateTime)) {
                        // Event has already passed, skip it
                        continue;
                    }

                    if (event.getParticipantIds().contains(userId)) {
                        eventList.add(event);
                    }
                }

                if (eventList.isEmpty()) {
                    // The list is empty, show the "No upcoming events" message
                    noEventsTextView.setVisibility(View.VISIBLE);
                    return; // Break out of the function if the list is empty
                } else  {
                    noEventsTextView.setVisibility(View.INVISIBLE);
                }

                // Sort the eventList by date
                Collections.sort(eventList, new Comparator<Event>() {
                    @Override
                    public int compare(Event event1, Event event2) {
                        // Assuming eventDate is stored as a string in "yyyy-MM-dd" format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        try {
                            Date date1 = dateFormat.parse(event1.getDate());
                            Date date2 = dateFormat.parse(event2.getDate());
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }
                });

                eventAdapter = new EventAdapter(eventList, HomeActivity.this);
                eventRecyclerView.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void InitViews() {
        eventCardView = findViewById(R.id.eventCardView);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        noEventsTextView = findViewById(R.id.noEventsTextView);
    }

    public static class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        List<Event> eventList;
        Activity activity;

        public EventAdapter(List<Event> eventList, Activity activity) {
            this.eventList = eventList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_layout, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = eventList.get(position);

            holder.eventNameTextView.setText(event.getName());
            holder.eventLocationTextView.setText(event.getLocation());
            holder.eventDateTextView.setText(event.getDate());
            holder.eventTimeTextView.setText(event.getTime());
            //holder.eventDescriptionTextView.setText(event.getDescription());

            // Set click listener on the ViewHolder
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve the clicked event
                    Event clickedEvent = eventList.get(holder.getAdapterPosition());

                    // Start the EventDisplay activity with event details
                    Intent intent = new Intent(activity, EventDisplay.class);
                    intent.putExtra("eventId", clickedEvent.getId());
                    intent.putExtra("eventName", clickedEvent.getName());
                    intent.putExtra("eventLocation", clickedEvent.getLocation());
                    intent.putExtra("eventDate", clickedEvent.getDate());
                    intent.putExtra("eventTime", clickedEvent.getTime());
                    intent.putExtra("eventDescription", clickedEvent.getDescription());
                    activity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public static class EventViewHolder extends RecyclerView.ViewHolder {
            public TextView eventNameTextView;
            public TextView eventLocationTextView;
            public TextView eventDateTextView;
            public TextView eventTimeTextView;
            //public TextView eventDescriptionTextView;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
                eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
                eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
                eventTimeTextView = itemView.findViewById(R.id.eventTimeTextView);
                //eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            }
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        super.onBackPressed();
    }
}









