package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class DashboardActivity extends AppCompatActivity {

    private CardView addAttendance,viewStudents,aboutUs;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addAttendance=(CardView)findViewById(R.id.addAttendance);
        viewStudents=(CardView)findViewById(R.id.viewStudents);
        aboutUs=(CardView)findViewById(R.id.aboutUs);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setTitle("Please Wait while upload and get the result...");
        progressDialog.setCanceledOnTouchOutside(false);

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
                        .setMinCropResultSize(512, 512)
                        .start(DashboardActivity.this);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please wait while we upload the image...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String userID = currentUser.getUid();

                File thumnbailFile = new File(resultUri.getPath());
                Bitmap thumbnail = null;

                try {
                    thumbnail = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)
                            .setQuality(100)
                            .compressToBitmap(thumnbailFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                final byte[] dataValue = byteArrayOutputStream.toByteArray();

                final StorageReference thumbPath = storageReference.child("profile_pictures").child("thumbs").child(userID + ".jpg");

                StorageReference filepath = storageReference.child("profile_pictures").child(userID + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            //final String downloadURI = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumbPath.putBytes(dataValue);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                       // String thumbURI = task.getResult().getDownloadUrl().toString();
                                        Map hashMap = new HashMap<>();
//                                        hashMap.put("image", downloadURI);
//                                        hashMap.put("thumbnail", thumbURI);

                                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                } else {
                                                    progressDialog.hide();
                                                    Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        progressDialog.hide();
                                        Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG);
                                    }
                                }
                            });


                        } else {
                            progressDialog.hide();
                            Toast.makeText(DashboardActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
