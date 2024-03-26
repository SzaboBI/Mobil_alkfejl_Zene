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

import java.util.ArrayList;

public class MainPageAfterLoggedIn extends AppCompatActivity {

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private ArrayList<song> songs;
    private SongAdapter songAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main_page_after_logged_in);
            recyclerView = findViewById(R.id.songRecyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(this,1));
            songs = new ArrayList<>();
            songAdapter = new SongAdapter(this,songs);
            recyclerView.setAdapter(songAdapter);
            initializeData();
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
    private void initializeData() {
        songs.add(new song("idni",1.5,"niin"));
        songs.add(new song("fdfd",2.5,"bu"));
        songAdapter.notifyDataSetChanged();
    }

    public void playSong(View view) {
        Log.d(MainPageAfterLoggedIn.class.getName(),"Lejatszas");
    }
}