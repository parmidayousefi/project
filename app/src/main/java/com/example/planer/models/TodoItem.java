
package com.example.myapp.models;

public class TodoItem {
    private String id;
    private String title;
    private boolean completed;

    public TodoItem() {
        // Required for Firestore
    }

    public TodoItem(String id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
