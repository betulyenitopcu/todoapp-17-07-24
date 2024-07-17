package com.example.todoapp;

public class Task {
    private String id;
    private String taskText;
    private boolean isChecked;

    // Parametresiz yapıcı
    public Task() {
    }

    // Parametreli yapıcı (id ve taskText)
    public Task(String id, String taskText) {
        this.id = id;
        this.taskText = taskText;
    }

    // Parametreli yapıcı (sadece taskText)
    public Task(String taskText) {
        this.taskText = taskText;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
