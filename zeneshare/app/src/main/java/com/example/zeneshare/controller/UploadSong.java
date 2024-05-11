package com.example.zeneshare.controller;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import com.example.zeneshare.R;
import com.example.zeneshare.adapter.AudioPickerContract;
import com.example.zeneshare.model.song;
import com.example.zeneshare.services.UploadService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Calendar;

public class UploadSong extends AppCompatActivity {

    private FirebaseUser user;
    private ActivityResultLauncher<String> audioPickerLauncher;
    private String filePath;

    private TextView TVfilePathShow;
    private EditText ETSongAuthor;
    private EditText ETSongTitle;

    private FirebaseFirestore songDB;
    private CollectionReference songRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && getIntent().getIntExtra("upload",1)==1){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_upload_song);
            audioPickerLauncher = registerForActivityResult(new AudioPickerContract(), this::onAudioFileSelected);
            TVfilePathShow = findViewById(R.id.songURL);
            ETSongAuthor = findViewById(R.id.songAuthor);
            ETSongTitle = findViewById(R.id.songTitle);
            songDB=FirebaseFirestore.getInstance();
            songRef = songDB.collection("Songs");
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        else if(user != null){
            finish();
            Intent openList = new Intent(this, MainPageAfterLoggedIn.class);
            startActivity(openList);
        }
        else {
            finish();
            Intent openLogin = new Intent(this, MainActivity.class);
            startActivity(openLogin);
        }

    }

    public void selectFile(View view) {
        audioPickerLauncher.launch("audio/*");
    }
    @SuppressLint("ResourceAsColor")
    private void onAudioFileSelected(Uri selectedAudioUri){
        filePath = selectedAudioUri.getPath();
        filePath =Environment.getExternalStorageDirectory() + "/" + filePath.split(":")[1];
        TVfilePathShow.setText(filePath);
        TVfilePathShow.setTextColor(R.color.black);

    }

    @SuppressLint("ResourceAsColor")
    public void upload(View view) {
        if (filePath == null){
            TVfilePathShow.setText("Adj meg egy fajlt!");
            TVfilePathShow.setTextColor(R.color.red);
        } else {
            String songAuthor = ETSongAuthor.getText().toString().trim();
            String songTitle = ETSongTitle.getText().toString().trim();
            String filename = songAuthor+"-"+songTitle+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE)+Calendar.getInstance().get(Calendar.SECOND);
            Intent intent = new Intent(this, UploadService.class);
            intent.putExtra("fileUri", filePath);
            intent.putExtra("filename", filename);
            //String[] filePathParts = filePath.split("/");
            songRef.add(new song(songTitle,songAuthor, filename, user.getEmail()))
                    .addOnSuccessListener(documentReference -> {
                        Log.i(UploadSong.class.getName(),"Letrehozva");
                    })
                    .addOnFailureListener(documentReference -> {
                        Log.e(UploadSong.class.getName(),"Error");
                    });
            startService(intent);
            finish();
            Intent intent2 = new Intent(this, MainPageAfterLoggedIn.class);
            startActivity(intent2);
        }

    }

}