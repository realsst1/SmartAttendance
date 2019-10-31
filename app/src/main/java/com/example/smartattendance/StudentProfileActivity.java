package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentProfileActivity extends AppCompatActivity {

    private String name,roll;
    private TextView studentName,studentRoll,studentAttendance;
    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        studentName=(TextView)findViewById(R.id.studentProfileName);
        studentRoll=(TextView)findViewById(R.id.studentProfileRoll);
        studentAttendance=(TextView)findViewById(R.id.studentProfileAttendance);

        name=getIntent().getStringExtra("name");
        roll=getIntent().getStringExtra("roll");

        studentRoll.setText(String.valueOf(Integer.parseInt(roll)+1));
        studentName.setText(name);

        attendanceRef= FirebaseDatabase.getInstance().getReference().child("attendance").child(roll);

        attendanceRef.keepSynced(true);
        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentAttendance.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                System.out.println(dataSnapshot.getChildrenCount())
;            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
