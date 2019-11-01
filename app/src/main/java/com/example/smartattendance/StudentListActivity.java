package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.smartattendance.Adapters.StudentListAdapter;
import com.example.smartattendance.Models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    private DatabaseReference studentRef;
    private ArrayList<Student> studentArrayList;
    private RecyclerView studentRecyclerView;
    private StudentListAdapter studentListAdapter;
    private LinearLayoutManager layoutManager;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        //setup views

        studentRecyclerView=(RecyclerView)findViewById(R.id.studentRecyclerView);
        layoutManager=new LinearLayoutManager(this);
        studentArrayList=new ArrayList<>();
        studentListAdapter=new StudentListAdapter(studentArrayList);
        studentRecyclerView.setLayoutManager(layoutManager);
        studentRecyclerView.setAdapter(studentListAdapter);
        searchText=(EditText)findViewById(R.id.searchText);

        studentRef= FirebaseDatabase.getInstance().getReference().child("students");


        studentRef.keepSynced(true);
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    String roll=d.child("roll").getValue().toString();
                    String name=d.child("name").getValue().toString();

                    studentArrayList.add(new Student(name,roll));
                    studentListAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });

    }

    public void filter(String text){
        ArrayList<Student> list=new ArrayList<>();
        for(Student s:studentArrayList){
            if(s.getName().toLowerCase().contains(text)){
                list.add(s);
            }
        }
        studentListAdapter.updateList(list);
    }
}
