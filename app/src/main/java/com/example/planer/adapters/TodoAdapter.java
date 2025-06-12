
package com.example.myapp.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.models.TodoItem;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todoList;

    public TodoAdapter(List<TodoItem> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.title.setText(item.getTitle());
        holder.checkbox.setChecked(item.isCompleted());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox checkbox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title);
            checkbox = itemView.findViewById(R.id.todo_checkbox);
        }
    }
}
