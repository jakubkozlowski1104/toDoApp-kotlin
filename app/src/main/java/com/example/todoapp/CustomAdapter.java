package com.example.todoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private ArrayList<String> originalTask_id, originalTitle, originalDescription, originalCategory, originalExecution_date, originalTask_status, originalCreated_at;
    private ArrayList<String> task_id, title, description, category, execution_date, task_status, created_at;
    private MyDatabaseHelper myDb;
    Button showNotifyButton;
    private Context mContext;

    public CustomAdapter(Activity activity, Context context, ArrayList<String> task_id, ArrayList<String> title, ArrayList<String> description, ArrayList<String> category, ArrayList<String> execution_date, ArrayList<String> task_status, ArrayList<String> created_at, MyDatabaseHelper myDb) {
        this.activity = activity;
        this.mContext = context;
        this.activity = activity;
        this.context = context;
        this.originalTask_id = new ArrayList<>(task_id);
        this.originalTitle = new ArrayList<>(title);
        this.originalDescription = new ArrayList<>(description);
        this.originalCategory = new ArrayList<>(category);
        this.originalExecution_date = new ArrayList<>(execution_date);
        this.originalTask_status = new ArrayList<>(task_status);
        this.originalCreated_at = new ArrayList<>(created_at);
        this.task_id = task_id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.execution_date = execution_date;
        this.task_status = task_status;
        this.created_at = created_at;
        this.myDb = myDb;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title_txt.setText(title.get(position));
        holder.category_txt.setText(category.get(position));
        holder.description_txt.setText(description.get(position));

        if (execution_date != null && position < execution_date.size() && execution_date.get(position) != null) {
            holder.execution_date_txt.setText(convertMillisToDate(Long.parseLong(execution_date.get(position))));
        } else {
            holder.execution_date_txt.setText("N/A");
        }

        if (created_at != null && position < created_at.size() && created_at.get(position) != null) {
            holder.showCreatedDate.setText("Created at: " + convertMillisToDate(Long.parseLong(created_at.get(position))));
        } else {
            holder.showCreatedDate.setText("Created at: N/A");
        }

        if (task_status != null && position < task_status.size()) {
            boolean isChecked = task_status.get(position).equals("1");
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(isChecked);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (position < task_id.size()) {
                        myDb.updateTaskStatus(task_id.get(position), isChecked);
                        holder.showStatus.setText(isChecked ? "1" : "0");
                    }
                }
            });
        }

        holder.showNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                String taskTitle = title.get(currentPosition);

                // Uruchom usługę powiadomień używając kontekstu z MainActivity
                Intent notificationIntent = new Intent(mContext, NotificationService.class);
                notificationIntent.putExtra("taskTitle", taskTitle);
                notificationIntent.putExtra("executionTimeMillis", Long.parseLong(execution_date.get(position)));
                mContext.startService(notificationIntent);

            }
        });
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

    public void filter(String text) {
        if (text.isEmpty()) {
            task_id.clear();
            title.clear();
            description.clear();
            category.clear();
            execution_date.clear();
            task_status.clear();
            created_at.clear();
            task_id.addAll(originalTask_id);
            title.addAll(originalTitle);
            description.addAll(originalDescription);
            category.addAll(originalCategory);
            execution_date.addAll(originalExecution_date);
            task_status.addAll(originalTask_status);
            created_at.addAll(originalCreated_at);
        } else {
            ArrayList<String> filteredIds = new ArrayList<>();
            ArrayList<String> filteredTitles = new ArrayList<>();
            ArrayList<String> filteredDescriptions = new ArrayList<>();
            ArrayList<String> filteredCategories = new ArrayList<>();
            ArrayList<String> filteredExecutionDates = new ArrayList<>();
            ArrayList<String> filteredTaskStatus = new ArrayList<>();
            ArrayList<String> filteredCreatedAt = new ArrayList<>();

            for (int i = 0; i < title.size(); i++) {
                if (title.get(i).toLowerCase().contains(text.toLowerCase())) {
                    filteredIds.add(task_id.get(i));
                    filteredTitles.add(title.get(i));
                    filteredDescriptions.add(description.get(i));
                    filteredCategories.add(category.get(i));
                    filteredExecutionDates.add(execution_date.get(i));
                    filteredTaskStatus.add(task_status.get(i));
                    filteredCreatedAt.add(created_at.get(i));
                }
            }

            task_id.clear();
            title.clear();
            description.clear();
            category.clear();
            execution_date.clear();
            task_status.clear();
            created_at.clear();
            task_id.addAll(filteredIds);
            title.addAll(filteredTitles);
            description.addAll(filteredDescriptions);
            category.addAll(filteredCategories);
            execution_date.addAll(filteredExecutionDates);
            task_status.addAll(filteredTaskStatus);
            created_at.addAll(filteredCreatedAt);
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_txt, category_txt, description_txt, execution_date_txt, showCreatedDate, showStatus;
        CheckBox checkBox;
        LinearLayout mainLayout;
        Button showNotifyButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title_txt = itemView.findViewById(R.id.taskTitle);
            category_txt = itemView.findViewById(R.id.taskCategory);
            description_txt = itemView.findViewById(R.id.taskDescription);
            execution_date_txt = itemView.findViewById(R.id.exDate);
            showCreatedDate = itemView.findViewById(R.id.showCreatedDate);
            checkBox = itemView.findViewById(R.id.checkBox);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            showNotifyButton = itemView.findViewById(R.id.showNotify);
        }
    }
}


