package com.example.smartattendance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartattendance.Models.Student;
import com.example.smartattendance.R;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private ArrayList<Student> studentList;
    private Context context;

    @NonNull
    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_single,parent,false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }



    }
}
