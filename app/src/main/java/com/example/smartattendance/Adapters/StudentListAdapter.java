package com.example.smartattendance.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartattendance.Models.Student;
import com.example.smartattendance.R;
import com.example.smartattendance.StudentProfileActivity;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private ArrayList<Student> studentList;
    private Context context;

    public StudentListAdapter(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_single,parent,false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.ViewHolder holder, int position) {

        final String name=studentList.get(position).getName();
        String img=studentList.get(position).getImageURL();
        final String roll=studentList.get(position).getRoll();

        holder.setStudentImage(img);
        holder.setStudentName(name);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, StudentProfileActivity.class);
                intent.putExtra("roll",roll);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });



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

        public void setStudentName(String name){
            TextView studentTextView=(TextView)view.findViewById(R.id.studentNameSingle);
            studentTextView.setText(name);
        }

        public void setStudentImage(String img){

        }



    }
}
