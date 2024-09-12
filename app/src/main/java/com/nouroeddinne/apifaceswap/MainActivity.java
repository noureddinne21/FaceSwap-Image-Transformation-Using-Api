package com.nouroeddinne.apifaceswap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_STORAGE = 1;

    ImageView imgF,imgT;
    Button swap;
    int imageSelect;
    String imageString1,imageString2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgF = findViewById(R.id.imageButton);
        imgT = findViewById(R.id.imageButton2);
        swap = findViewById(R.id.button);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }


        imgF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseImg();
                imageSelect=1;

            }
        });


        imgT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseImg();
                imageSelect=2;

            }
        });


        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> parameters = new HashMap<>();
                parameters.put("TargetImageBase64Data", imageString1);
                parameters.put("SourceImageBase64Data", imageString2);

                OkHttpMapExample example = new OkHttpMapExample();
                example.sendRequest(parameters, new OkHttpMapExample.Callback() {
                    @Override
                    public void onSuccess(String url) {
                        System.out.println("Result Image : " + url);
                        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                        intent.putExtra("url",url);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });


            }
        });










    }


    public void choseImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchResult.launch(intent);
    }


    ActivityResultLauncher<Intent> launchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {

                    if(o!=null&&o.getResultCode()==RESULT_OK) {
                        Uri selectedfile = o.getData().getData();
                        if (selectedfile != null) {

                            if(imageSelect==1){
                                Picasso.get().load(selectedfile).into(imgF);
                            }else{
                                Picasso.get().load(selectedfile).into(imgT);
                            }

                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedfile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            byte[] byteArray = outputStream.toByteArray();

                            //Use your Base64 String as you wish

                            if(imageSelect==1){
                                imageString1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            }else{
                                imageString2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            }

                        }
                    }

                }
            });




























}