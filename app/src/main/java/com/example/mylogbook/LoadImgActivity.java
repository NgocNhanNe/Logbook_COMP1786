package com.example.mylogbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mylogbook.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadImgActivity extends AppCompatActivity {

    ImageView back_btn;
    ImageView imageViewFromURL;
    Button next_btn,pre_btn;

    int index;
    public ArrayList<String> URLs = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_img);

        imageViewFromURL = findViewById(R.id.img);
        next_btn = findViewById(R.id.next_button);
        pre_btn = findViewById(R.id.previous_button);
        back_btn = findViewById(R.id.back_btn);

        try {
            loadImg();
            setImageFromURL();
        } catch (IOException e) {
            e.printStackTrace();
        }
        next_btn.setOnClickListener(view -> {
            int total = URLs.size();
            if(total>0){
                index++;
                if(index == total){
                    index = 0;
                }
                setImageFromURL();
            }
        });
        pre_btn.setOnClickListener(view -> {
            int total = URLs.size();
            if(total>0){
                index--;
                if(index < 0){
                    index = total - 1;
                }
                setImageFromURL();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoadImgActivity.this,
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    //load Image from URL link
    public void loadImg() throws IOException {
        //read url from file and add list URLs
        FileInputStream fileInputStream = getApplicationContext().openFileInput("storeImgToFile.txt");
        if (fileInputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineData = bufferedReader.readLine();
            while (lineData != null) {
                URLs.add(lineData);
                lineData = bufferedReader.readLine();
            }
        }

    }

    //set image in array URLs
    private void  setImageFromURL(){
            Glide.with(this).load(URLs.get(index)).into(imageViewFromURL);
    }
}