
package com.example.myapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.adapters.TodoAdapter;
import com.example.myapp.models.TodoItem;
import com.example.myapp.alarms.AlarmReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference todosRef;
    private List<TodoItem> todoList;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_media) {
                startActivity(new Intent(this, com.example.myapp.activities.MediaActivity.class));
                return true;
            }
            return true;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        todosRef = db.collection("users").document(user.getUid()).collection("todos");

        RecyclerView recyclerView = findViewById(R.id.todo_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();
        adapter = new TodoAdapter(todoList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddTodoDialog());

        loadTodos();
    }

    private void loadTodos() {
        todosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            todoList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                TodoItem item = doc.toObject(TodoItem.class);
                todoList.add(item);
            }
            adapter.notifyDataSetChanged();

            if (todoList.isEmpty()) {
                Toast.makeText(this, "No tasks yet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTodoDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter task title");

        new android.app.AlertDialog.Builder(this)
            .setTitle("New Task")
            .setView(input)
            .setPositiveButton("Next", (dialog, which) -> {
                String title = input.getText().toString().trim();
                if (!title.isEmpty()) {
                    showDateTimePicker(title);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showDateTimePicker(String title) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (tpView, hour, minute) -> {
                calendar.set(year, month, day, hour, minute);

                String id = UUID.randomUUID().toString();
                TodoItem newItem = new TodoItem(id, title, false);
                todosRef.document(id).set(newItem).addOnSuccessListener(unused -> {
                    todoList.add(newItem);
                    adapter.notifyItemInserted(todoList.size() - 1);
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();

                    setAlarm(calendar, title);
                });

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void setAlarm(Calendar calendar, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);

        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
