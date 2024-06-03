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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> task_id, title, description, category;
    private ArrayList<String> execution_date;  // Dodaj to pole
    int position;
    Activity activity;

    public CustomAdapter(Activity activity, Context context, ArrayList<String> task_id, ArrayList<String> title, ArrayList<String> description, ArrayList<String> category, ArrayList<String> execution_date) {
        this.activity = activity;
        this.context = context;
        this.task_id = task_id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.execution_date = execution_date;  // Dodaj to pole
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.position = position;
        holder.task_id_txt.setText(String.valueOf(task_id.get(position)));
        holder.title_txt.setText(String.valueOf(title.get(position)));
        holder.category_txt.setText(String.valueOf(category.get(position)));
        holder.description_txt.setText(String.valueOf(description.get(position)));

        // Sprawdzenie czy lista execution_date nie jest pusta i czy zawiera poprawne dane
        if (execution_date != null && position < execution_date.size() && execution_date.get(position) != null) {
            // Konwersja czasu w formacie millis na datę
            holder.execution_date_txt.setText(convertMillisToDate(Long.parseLong(execution_date.get(position))));
        } else {
            // Ustawienie domyślnego tekstu, jeśli nie można parsować daty
            holder.execution_date_txt.setText("N/A");
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(task_id.get(position)));
                intent.putExtra("title", String.valueOf(title.get(position)));
                intent.putExtra("category", String.valueOf(category.get(position)));
                intent.putExtra("description", String.valueOf(description.get(position)));

                // Sprawdzenie czy lista execution_date nie jest pusta i czy zawiera poprawne dane
                if (execution_date != null && position < execution_date.size() && execution_date.get(position) != null) {
                    intent.putExtra("execution_date", String.valueOf(execution_date.get(position)));
                } else {
                    intent.putExtra("execution_date", "N/A");
                }

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
        TextView task_id_txt, title_txt, description_txt, category_txt, execution_date_txt;  // Dodaj to pole
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            task_id_txt = itemView.findViewById(R.id.taskId);
            title_txt = itemView.findViewById(R.id.taskTitle);
            description_txt = itemView.findViewById(R.id.taskDescription);
            category_txt = itemView.findViewById(R.id.taskCategory);
            execution_date_txt = itemView.findViewById(R.id.exDate);  // Dodaj to pole
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
