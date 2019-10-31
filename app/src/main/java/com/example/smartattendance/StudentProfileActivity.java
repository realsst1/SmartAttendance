package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class StudentProfileActivity extends AppCompatActivity {

    private String name,roll;
    private TextView studentName,studentRoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        studentName=(TextView)findViewById(R.id.studentProfileName);
        studentRoll=(TextView)findViewById(R.id.studentProfileRoll);

        name=getIntent().getStringExtra("name");
        roll=getIntent().getStringExtra("roll");

        studentRoll.setText(String.valueOf(Integer.parseInt(roll)+1));
        studentName.setText(name);

    }
}
