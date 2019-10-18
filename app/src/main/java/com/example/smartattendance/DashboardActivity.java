package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    private CardView addAttendance,viewStudents,aboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addAttendance=(CardView)findViewById(R.id.addAttendance);
        viewStudents=(CardView)findViewById(R.id.viewStudents);
        aboutUs=(CardView)findViewById(R.id.aboutUs);

        //view Students

        viewStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,StudentListActivity.class));
            }
        });
    }
}
