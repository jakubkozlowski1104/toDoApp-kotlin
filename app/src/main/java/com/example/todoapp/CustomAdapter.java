package com.example.todoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Activity activity;
    private Context context;
    private ArrayList<String> task_id, title, description, category, execution_date, task_status;
    private MyDatabaseHelper myDb;
    int position;


    public CustomAdapter(Activity activity, Context context, ArrayList<String> task_id, ArrayList<String> title, ArrayList<String> description, ArrayList<String> category, ArrayList<String> execution_date, ArrayList<String> task_status, MyDatabaseHelper myDb) {
        this.activity = activity;
        this.context = context;
        this.task_id = task_id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.execution_date = execution_date;
        this.task_status = task_status;
        this.myDb = myDb;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.currentPosition = viewType;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.checkBox.setChecked(false); // Ustawienie domyślnej wartości na false
        holder.showStatus.setText("0"); // Ustawienie domyślnego statusu na "0"
        holder.currentPosition = position;


        // Ustawienie stanu checkboxa i statusu na podstawie danych
        if (task_status != null && position < task_status.size()) {
            boolean isChecked = task_status.get(position).equals("1"); // Sprawdzenie, czy status w bazie danych to 1
            holder.checkBox.setChecked(isChecked); // Ustawienie stanu checkboxa
            holder.showStatus.setText(task_status.get(position)); // Ustawienie statusu w TextView
        }

        // Obsługa zdarzenia kliknięcia checkboxa
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Aktualizacja statusu zadania w bazie danych
                myDb.updateTaskStatus(task_id.get(position), isChecked);

                // Aktualizacja wyświetlanego statusu w TextView
                holder.showStatus.setText(isChecked ? "1" : "0");
            }
        });

        // Pozostała część onBindViewHolder
        holder.title_txt.setText(title.get(position));
        holder.category_txt.setText(category.get(position));
        holder.description_txt.setText(description.get(position));

        if (execution_date != null && position < execution_date.size() && execution_date.get(position) != null) {
            holder.execution_date_txt.setText(convertMillisToDate(Long.parseLong(execution_date.get(position))));
        } else {
            holder.execution_date_txt.setText("N/A");
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", task_id.get(position));
                intent.putExtra("title", title.get(position));
                intent.putExtra("description", description.get(position));
                intent.putExtra("category", category.get(position));
                intent.putExtra("execution_date", execution_date.get(position));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return task_id.size();
    }

    private String convertMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView showStatus;
        TextView title_txt, category_txt, description_txt, execution_date_txt;
        LinearLayout mainLayout;
        int currentPosition;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            showStatus = itemView.findViewById(R.id.showStatus);
            title_txt = itemView.findViewById(R.id.taskTitle);
            category_txt = itemView.findViewById(R.id.taskCategory);
            description_txt = itemView.findViewById(R.id.taskDescription);
            execution_date_txt = itemView.findViewById(R.id.exDate);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}
