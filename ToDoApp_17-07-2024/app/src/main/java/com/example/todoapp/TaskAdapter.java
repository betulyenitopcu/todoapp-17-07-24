package com.example.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Bind data to UI elements
        Task task = taskList.get(position);
        holder.taskTextView.setText(task.getTaskText());
        holder.checkBox.setChecked(task.isChecked());

        // Set click listener for checkbox
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setChecked(holder.checkBox.isChecked());
                // Update the task's isChecked field in Firebase
                DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(task.getId());
                tasksRef.child("isChecked").setValue(task.isChecked());
            }
        });

        // Set click listener for edit button
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example edit functionality: show a Toast message
                // You can replace this with an actual edit dialog
                Toast.makeText(context, "Edit task: " + task.getTaskText(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the task from Firebase
                DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(task.getId());
                tasksRef.removeValue();

                // Remove the task from the list and notify the adapter
                taskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, taskList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the task list
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTextView;
        public CheckBox checkBox;
        public Button editButton;
        public Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI elements
            taskTextView = itemView.findViewById(R.id.taskTextView);
            checkBox = itemView.findViewById(R.id.todoCheckBox); // Ensure R.id.todoCheckBox is correct
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public void addTask(Task task) {
        // Add a new task to the list
        taskList.add(task);
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }
}
