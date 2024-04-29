package com.example.zeneshare.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zeneshare.R;
import com.example.zeneshare.adapter.SongAdapter;
import com.example.zeneshare.model.song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainPageAfterLoggedIn extends AppCompatActivity {

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<song> songs;
    private SongAdapter songAdapter;
    private FirebaseFirestore songDB;
    private CollectionReference songItems;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main_page_after_logged_in);
            songDB=FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            songItems = songDB.collection("Songs");
            recyclerView = findViewById(R.id.songRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(this,1));
            songs = new ArrayList<>();
            songAdapter = new SongAdapter(this,songs);
            recyclerView.setAdapter(songAdapter);
            getSongsFromDB();
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

        }
        else {
            finish();
            Intent openLogin = new Intent(this,MainActivity.class);
            startActivity(openLogin);
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private void getSongsFromDB() {
        songs.clear();
        songItems.orderBy("title").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                song song1 = document.toObject(song.class);
                songs.add(song1);
            }
            if (songs.isEmpty()){
                initializeData();
                getSongsFromDB();
            }
            songAdapter.notifyDataSetChanged();
        });

    }


    private void initializeData() {
        songItems.add(new song("Diamonds","Rihanna","rihanna-diamonds.mp3","System"));
        songItems.add(new song("Dont stop the music","Rihanna","rihanna-dontstopthemusic.mp3","System"));

    }

    public void playSong(View view) {
        Log.d(MainPageAfterLoggedIn.class.getName(),"Lejatszas");
    }

    public void openUpload(View view) {
        finish();
        Intent openUpload = new Intent(this, UploadSong.class);
        openUpload.putExtra("upload",1);
        startActivity(openUpload);
    }
}