package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {

    TextInputEditText inputEventName, inputLocation, inputTime, inputDate, inputDescription;
    TextInputLayout eventNameLayout, eventLocationLayout, eventDateLayout, eventTimeLayout, eventDescriptionLayout;
    MaterialButton btnAddEvent;
    BottomNavigationView bottomNavigationView;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        initViews();

        bottomNavigationView.setSelectedItemId(R.id.toAdd);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.toHome:
                    Intent homeEventIntent = new Intent(AddEventActivity.this, HomeActivity.class);
                    startActivity(homeEventIntent);
                    break;
                case R.id.toAdd:
                    // Do nothing since we're already in the AddEventActivity
                    break;
                case R.id.toJoin:
                    Intent joinEventIntent = new Intent(AddEventActivity.this, JoinEvent.class);
                    startActivity(joinEventIntent);
                    break;
            }
            return true;
        });


        inputDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                    String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                                    inputDate.setText(selectedDate);
                                }
                            }, year, month, dayOfMonth);
                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); //Set the minimum date to today's date
                    datePickerDialog.show();
                }
                return true;
            }
        });

        inputTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                    String selectedTime = String.format("%02d:%02d", hour, minute);
                                    inputTime.setText(selectedTime);
                                }
                            }, hour, minute, true);

                    timePickerDialog.show();
                }
                return true;
            }
        });


        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                String userId = currentUser.getUid();

                String eventName = inputEventName.getText().toString();
                String eventLocation = inputLocation.getText().toString();
                String eventDate = inputDate.getText().toString();
                String eventTime = inputTime.getText().toString();
                String eventDescription = inputDescription.getText().toString();

                if (!eventName.isEmpty() && !eventLocation.isEmpty() && !eventDate.isEmpty() && !eventTime.isEmpty()) {
                    String eventId = mDatabase.push().getKey();

                    List<String> participantIds = new ArrayList<>();
                    List<EquipmentItem> equipmentList = new ArrayList<>();
                    participantIds.add(userId); // Add the current user to the participantIds list

                    Event event = new Event(eventId, eventName, eventLocation, eventDate, eventTime, eventDescription, participantIds, equipmentList);

                    mDatabase.child("events").child(eventId).setValue(event);
                    Toast.makeText(AddEventActivity.this, "Event saved succesfully", Toast.LENGTH_SHORT).show();

                    // Create an intent to start the EventDetailsActivity
                    Intent intent = new Intent(AddEventActivity.this, EventDisplay.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("eventName", eventName);
                    intent.putExtra("eventLocation", eventLocation);
                    intent.putExtra("eventDate", eventDate);
                    intent.putExtra("eventTime", eventTime);
                    intent.putExtra("eventDescription", eventDescription);
                    startActivity(intent); //sends params to EventDisplay
                    startActivity(new Intent(AddEventActivity.this, HomeActivity.class)); //move to Home page

                } else {
                    if (eventName.isEmpty())
                        eventNameLayout.setError("Empty input");
                    if (eventLocation.isEmpty())
                        eventLocationLayout.setError("Empty input");
                    if (eventDate.isEmpty())
                        eventDateLayout.setError("Empty input");
                    if (eventTime.isEmpty())
                        eventTimeLayout.setError("Empty input");
                    if (eventDescription.isEmpty())
                        eventDescriptionLayout.setError("Empty input");
                }

            }
        });
    }

    private void initViews() {
        eventNameLayout = findViewById(R.id.eventNameLayout);
        eventLocationLayout = findViewById(R.id.eventLocationLayout);
        eventDateLayout = findViewById(R.id.eventDateLayout);
        eventTimeLayout = findViewById(R.id.eventTimeLayout);
        eventDescriptionLayout = findViewById(R.id.eventDescriptionLayout);
        inputEventName = findViewById(R.id.inputEventName);
        inputLocation = findViewById(R.id.inputLocation);
        inputDate = findViewById(R.id.inputDate);
        inputTime = findViewById(R.id.inputTime);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        inputDescription = findViewById(R.id.inputDescription);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

}
