package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Button buttonAddTask;
    private Button buttonLogout;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference tasksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        tasksRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(currentUser.getUid());

        // Initialize UI components
        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, MainActivity.this); // MainActivity.this olarak değiştirildi

        // Set RecyclerView layout manager and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Set click listener for add task button
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskText = editTextTask.getText().toString().trim();
                if (!taskText.isEmpty()) {
                    // Create a Task object
                    String taskId = tasksRef.push().getKey();

                    Task newTask = new Task(taskId,taskText);

                    // Push the new task to Firebase Realtime Database
                    tasksRef.push().setValue(newTask);

                    // Clear the EditText
                    editTextTask.setText("");
                }
                else
               {
                   Toast.makeText(MainActivity.this, "BOŞ VERİ EKLEYEMEZSİNİZ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user and navigate to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optionally call finish() to prevent the user from returning to this activity
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If user is not signed in, navigate to login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            // If user is signed in, load their tasks from Firebase
            tasksRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    taskList.clear();
                    for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                        String id = taskSnapshot.getKey();
                        Task task = taskSnapshot.getValue(Task.class);
                        if (task != null) {
                            task.setId(id);
                            taskList.add(task);
                        }
                    }
                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Failed to load tasks.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
