package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class DashboardActivity extends AppCompatActivity {

    private CardView addAttendance,viewStudents,aboutUs;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String text;
    private ArrayList<String> studentLabels=new ArrayList<>();
    private HashMap<String,String> studentMap=new HashMap<>();
    private Date today;
    private SimpleDateFormat sdfToday;
    private String dateToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addAttendance=(CardView)findViewById(R.id.addAttendance);
        viewStudents=(CardView)findViewById(R.id.viewStudents);
        aboutUs=(CardView)findViewById(R.id.aboutUs);
        progressDialog=new ProgressDialog(this);

        today=Calendar.getInstance().getTime();
        sdfToday=new SimpleDateFormat("dd-MM-yyyy");
        dateToSearch=sdfToday.format(today);
        System.out.println("Today:"+sdfToday.format(today));


        databaseReference= FirebaseDatabase.getInstance().getReference().child("attendance");
        databaseReference.keepSynced(true);

        //view Students

        viewStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,StudentListActivity.class));
            }
        });

        //about us

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,AboutUsActivity.class));
            }
        });

        addAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMinCropResultSize(1024, 1024)
                        .start(DashboardActivity.this);
            }
        });


        try{
            InputStream inputStream=getAssets().open("retrained_labels.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            while((text=reader.readLine())!=null){
                studentLabels.add(text);
            }
            //text=new String(buffer);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(studentLabels);
        for(int i=0;i<studentLabels.size();i++){
            studentMap.put(Integer.toString(i),studentLabels.get(i));
        }
        System.out.println(studentMap);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Analyzing Image");
                progressDialog.setMessage("Please wait while we analyze the image...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                System.out.println(resultUri.getPath());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String userID = currentUser.getUid();

                File thumnbailFile = new File(resultUri.getPath());
                Bitmap thumbnail = BitmapFactory.decodeFile(thumnbailFile.getAbsolutePath());



                thumbnail=Bitmap.createScaledBitmap(thumbnail,224,224,false);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//                final byte[] dataValue = byteArrayOutputStream.toByteArray();

                //final StorageReference thumbPath = storageReference.child("profile_pictures").child("thumbs").child(userID + ".jpg");

                int intValues[]=new int[thumbnail.getHeight()*thumbnail.getWidth()];
                float floatValues[]=new float[thumbnail.getHeight()*thumbnail.getWidth()*3];
                thumbnail.getPixels(intValues, 0, thumbnail.getWidth(), 0, 0, thumbnail.getWidth(), thumbnail.getHeight());

                for (int i = 0; i < intValues.length; ++i) {
                    final int val = intValues[i];
                    floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
                    floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
                }

                float res[]=new float[72];
                TensorFlowInferenceInterface inferenceInterface=new TensorFlowInferenceInterface(getAssets(),"file:///android_asset/retrained_graph.pb");
                inferenceInterface.feed("Placeholder",floatValues,1,224,224,3);
                inferenceInterface.run(new String[]{"final_result"});


                inferenceInterface.fetch("final_result",res);

                System.out.println("success"+res[0]);

                float max=Float.MIN_VALUE;
                int pos=0;
                ArrayList<Float> list=new ArrayList<>();
                for(int i=0;i<res.length;i++){
                    if(res[i]>max) {
                        max = res[i];
                        pos=i;
                    }
                    System.out.println(res[i]);
                    list.add(res[i]);
                }

                System.out.println("List\n"+list);
                System.out.println("Pos:"+pos);
                System.out.println("Max:"+ Collections.max(list));
                System.out.println("Identified:"+studentMap.get(String.valueOf(pos)));


                //add attendance record

                final int finalPos = pos;
                databaseReference.child(String.valueOf(pos)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean flag=false;
                        for(DataSnapshot d:dataSnapshot.getChildren()){
                            String dateToCompare=d.child("date").getValue().toString();
                            if(dateToCompare.equals(dateToSearch)){
                                flag=true;
                                break;
                            }
                        }
                        if(flag==false){
                            HashMap<String,String> dateMap=new HashMap<>();
                            dateMap.put("date",dateToSearch);
                            databaseReference.child(String.valueOf(finalPos)).push().setValue(dateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(DashboardActivity.this, "Attendance added successfully", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        Toast.makeText(DashboardActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(DashboardActivity.this, "Attendance already taken for today", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






//                StorageReference filepath = storageReference.child("profile_pictures").child(userID + ".jpg");
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            //final String downloadURI = task.getResult().getDownloadUrl().toString();
//                            UploadTask uploadTask = thumbPath.putBytes(dataValue);
//                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                    if (task.isSuccessful()) {
//
//                                       // String thumbURI = task.getResult().getDownloadUrl().toString();
//                                        Map hashMap = new HashMap<>();
////                                        hashMap.put("image", downloadURI);
////                                        hashMap.put("thumbnail", thumbURI);
//
//                                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    progressDialog.dismiss();
//                                                } else {
//                                                    progressDialog.hide();
//                                                    Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
//
//                                    } else {
//                                        progressDialog.hide();
//                                        Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG);
//                                    }
//                                }
//                            });
//
//
//                        } else {
//                            progressDialog.hide();
//                            Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
