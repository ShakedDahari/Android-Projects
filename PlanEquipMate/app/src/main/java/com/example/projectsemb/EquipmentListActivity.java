package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EquipmentListActivity extends AppCompatActivity {

    MaterialButton btnAddEquip;
    EditText editTextEquipmentName;
    List<EquipmentItem> equipmentList;
    RecyclerView recyclerViewEquipment;
    EquipmentAdapter equipmentAdapter;
    FirebaseAuth mAuth;
    boolean isEditTextVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list);

        btnAddEquip = findViewById(R.id.btnAddEquip);
        editTextEquipmentName = findViewById(R.id.editTextEquipmentName);


        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference eventRef  = FirebaseDatabase.getInstance().getReference("events");
        Intent i = getIntent();
        if (i != null) {
            String eventId = i.getStringExtra("eventId");
            eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        }

        // Initialize the equipment list and adapter
        equipmentList = new ArrayList<>();
        equipmentAdapter = new EquipmentAdapter(equipmentList, eventRef, userId);

        // Configure the RecyclerView
        recyclerViewEquipment = findViewById(R.id.recyclerViewEquipment);

        recyclerViewEquipment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEquipment.setAdapter(equipmentAdapter);

        // Retrieve the event details from intent
        Intent intent = getIntent();
        if (intent != null) {
            String eventId = intent.getStringExtra("eventId");
            retrieveEquipmentList(eventId);
        }


        btnAddEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditTextVisible) {
                    // Perform addition to the list
                    String equipmentName = editTextEquipmentName.getText().toString();
                    if (!TextUtils.isEmpty(equipmentName)) {
                        // Retrieve the event details from intent
                        Intent intent = getIntent();
                        if (intent != null) {
                            String eventId = intent.getStringExtra("eventId");
                            // Add the equipmentName to the list or perform the desired action
                            addEquipmentItem(eventId, equipmentName);
                        }
                    }
                    // Clear the EditText
                    editTextEquipmentName.setText("");
                    // Hide the EditText
                    editTextEquipmentName.setVisibility(View.INVISIBLE);
                    // Update the button text
                    btnAddEquip.setText("add");
                    // Update the visibility state
                    isEditTextVisible = false;
                } else {
                    // Show the EditText
                    editTextEquipmentName.setVisibility(View.VISIBLE);
                    // Update the button text
                    btnAddEquip.setText("save");
                    // Update the visibility state
                    isEditTextVisible = true;
                }
            }
        });
    }

    private void retrieveFullName(String userId) {
        DatabaseReference userRef  = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("fullName")) {
                    DataSnapshot fn = snapshot.child("fullName");
                    String fullName = fn.getValue(String.class);
                    updateUserInAdapter(fullName);
                } else {
                    // The fullName value doesn't exist in the database
                    Toast.makeText(EquipmentListActivity.this, "No full name", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(EquipmentListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateUserInAdapter(String fullName) {
        // Pass the fullName to the adapter
        equipmentAdapter.setFullName(fullName);
        equipmentAdapter.notifyDataSetChanged();
    }

    private void retrieveEquipmentList(String eventId) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                equipmentList.clear();
                if (snapshot.hasChild("equipmentList")) {
                    DataSnapshot equipmentListSnapshot = snapshot.child("equipmentList");
                    for (DataSnapshot itemSnapshot : equipmentListSnapshot.getChildren()) {
                        EquipmentItem equipmentItem = itemSnapshot.getValue(EquipmentItem.class);
                        equipmentList.add(equipmentItem);
                    }
                    equipmentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EquipmentListActivity.this, "No equipment list", Toast.LENGTH_SHORT).show();
                }

                retrieveFullName(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(EquipmentListActivity.this, "No equipment list", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addEquipmentItem(String eventId, String equipmentName) {
        EquipmentItem item = new EquipmentItem(equipmentName, false, "", "");

        if (!equipmentName.isEmpty()) {
            equipmentList.add(item);
            equipmentAdapter.notifyDataSetChanged();

            // Update the equipment list in the database
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
            eventRef.child("equipmentList").setValue(equipmentList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EquipmentListActivity.this, "Equipment item added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EquipmentListActivity.this, "Failed to add equipment item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

        List<EquipmentItem> equipmentList;
        DatabaseReference eventRef;
        String fullName;
        String userId;

        public EquipmentAdapter(List<EquipmentItem> equipmentList, DatabaseReference eventRef, String userId) {
            this.equipmentList = equipmentList;
            this.eventRef = eventRef;
            this.userId = userId;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_equipment, parent, false);
            return new EquipmentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
            EquipmentItem equipmentItem = equipmentList.get(position);
            holder.bind(equipmentItem);
        }

        @Override
        public int getItemCount() {
            return equipmentList.size();
        }

        public class EquipmentViewHolder extends RecyclerView.ViewHolder {

            MaterialCheckBox checkboxEquipment;
            TextView textEquipmentName;
            TextView textUserName;
            RecyclerView recyclerViewEquipment;
            ImageView imageViewDelete;

            public EquipmentViewHolder(@NonNull View itemView) {
                super(itemView);

                checkboxEquipment = itemView.findViewById(R.id.checkboxEquipment);
                textEquipmentName = itemView.findViewById(R.id.textEquipmentName);
                textUserName = itemView.findViewById(R.id.textUserName);
                recyclerViewEquipment = itemView.findViewById(R.id.recyclerViewEquipment);
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            }

            public void bind(EquipmentItem equipmentItem) {
                checkboxEquipment.setChecked(equipmentItem.isSelected());
                textEquipmentName.setText(equipmentItem.getName());
                textUserName.setText(equipmentItem.getUserName());
                imageViewDelete.setTag(getAdapterPosition());

                checkboxEquipment.setEnabled(true);
                checkboxEquipment.setOnCheckedChangeListener(null);

                if (equipmentItem.isSelected()) {
                    if (equipmentItem.getUserId() != null && equipmentItem.getUserId().equals(userId)) {
                        // Current user checked the item, enable the checkbox and handle its changes
                        checkboxEquipment.setEnabled(true);
                        checkboxEquipment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            equipmentItem.setSelected(isChecked);

                            if (isChecked) {
                                // Set the user's full name as textUserName
                                equipmentItem.setUserName(fullName);
                                equipmentItem.setUserId(userId);
                                textUserName.setText(fullName);
                            } else {
                                // If unchecked, clear the user name
                                equipmentItem.setUserName("");
                                equipmentItem.setUserId("");
                                textUserName.setText("");
                            }

                            // Update Firebase accordingly
                            DatabaseReference equipmentRef = eventRef.child("equipmentList").child(String.valueOf(getAdapterPosition()));
                            equipmentRef.setValue(equipmentItem);
                        });
                    } else {
                        // Other user checked the item, disable the checkbox
                        checkboxEquipment.setEnabled(false);
                    }
                } else {
                    // Item is not checked, enable the checkbox for everyone
                    checkboxEquipment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        equipmentItem.setSelected(isChecked);

                        if (isChecked) {
                            // Set the user's full name as textUserName
                            equipmentItem.setUserName(fullName);
                            equipmentItem.setUserId(userId);
                            textUserName.setText(fullName);
                        } else {
                            // If unchecked, clear the user name
                            equipmentItem.setUserName("");
                            equipmentItem.setUserId("");
                            textUserName.setText("");
                        }

                        // Update Firebase accordingly
                        DatabaseReference equipmentRef = eventRef.child("equipmentList").child(String.valueOf(getAdapterPosition()));
                        equipmentRef.setValue(equipmentItem);
                    });
                }

                // Handle the delete icon click event
                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        showDeleteAlertDialog(position);
                    }
                });
            }
        }

        private void showDeleteAlertDialog(int position) {
            // Create and show the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentListActivity.this);
            builder.setTitle("Delete Equipment");
            builder.setMessage("Are you sure you want to delete this equipment?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete the item from the list
                    equipmentList.remove(position);
                    equipmentAdapter.notifyDataSetChanged();

                    // Update the equipment list in the database
                    String eventId = getIntent().getStringExtra("eventId");
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
                    eventRef.child("equipmentList").setValue(equipmentList)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Successfully updated the equipment list in the database
                                        Toast.makeText(EquipmentListActivity.this, "Equipment item deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to update the equipment list in the database
                                        Toast.makeText(EquipmentListActivity.this, "Failed to delete equipment item", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    // Show a toast to indicate successful deletion
                    Toast.makeText(EquipmentListActivity.this, "Equipment item deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();
        }
    }
}