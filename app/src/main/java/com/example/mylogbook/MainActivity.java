package com.example.mylogbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mylogbook.databinding.ActivityMainBinding;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    ProgressDialog progressDialog;
    int index;
    Button  add_btn, reset_btn, gotoLoad,gotoCamera;
    EditText urlText;
    ImageView imgImport;
    public ArrayList<String> URLs = new ArrayList<>();

    String regex = "(http)?s?:?(\\/\\/[^\"']*\\.(?:png|jpg|jpeg|gif|png|svg))"; //regex for image url

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlText = findViewById(R.id.url_edit);
        add_btn = findViewById(R.id.add_button);
        reset_btn = findViewById(R.id.resset_button);
        imgImport = findViewById(R.id.img_import);
        gotoLoad = findViewById(R.id.gotoLoadActivity);
        gotoCamera = findViewById(R.id.gotoCameraActivity);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImageFile();
            }
        });

        whenClickAdd();


        gotoLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,
                        LoadImgActivity.class);
                startActivity(i);
                finish();
            }
        });

        gotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,
                        CameraActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void whenClickAdd() {
        add_btn.setOnClickListener(v -> {
            String imageURL = urlText.getText().toString().trim();
            Pattern p = Pattern.compile(regex);
            Matcher image = p.matcher(imageURL);
            if (imageURL.isEmpty()) {
                urlText.setError("Please enter a URL");
                urlText.requestFocus();
            } else {
                if (image.matches()) {
                    URLs.add(imageURL);
                    try {
                        storeToFile(imageURL);
                        Toast.makeText(this, "URL added successfully", Toast.LENGTH_SHORT).show();
                        Glide.with(this)
                                .load(imageURL)
                                .into(imgImport);
                        urlText.setText("");
                        index = URLs.indexOf(imageURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Save File Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    urlText.setError("Please enter invalid URL");
                    urlText.requestFocus();
                }
            }
        });
    }

    //store image file
    private void storeToFile(String urLnew) throws IOException {
        URLs.add(urLnew);
        new FetchImg(URLs).start();

        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("storeImgToFile.txt", Context.MODE_APPEND);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write(urLnew);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStreamWriter.close();
    }

    class FetchImg extends Thread{
        public ArrayList<String> URLs = new ArrayList<>();
        Bitmap bitmap;
        FetchImg(ArrayList<String> URLs){
            this.URLs = URLs;
        }

        public void run(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Getting a pic..." );
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            InputStream inputStream = null;
            try {
                inputStream = new java.net.URL(URLs.get(index)).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                        imgImport.setImageBitmap(bitmap);
                    }
                }
            });
        }

    }

    public void deleteImageFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Image?");
        builder.setMessage("Are you sure you want to delete all Images?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                URLs.clear();
                imgImport.setImageResource(0);
                removeFile();
                index = 0;
                Toast.makeText(MainActivity.this, "All Images deleted successfully!", Toast.LENGTH_SHORT).show();
            //Refresh Activity
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    private void removeFile() {
        getApplicationContext().deleteFile("storeImgToFile.txt");
    }
}