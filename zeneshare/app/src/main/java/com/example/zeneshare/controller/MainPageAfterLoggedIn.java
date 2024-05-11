package com.example.zeneshare.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zeneshare.R;
import com.example.zeneshare.adapter.SongAdapter;
import com.example.zeneshare.adapter.SongClickListener;
import com.example.zeneshare.model.song;
import com.example.zeneshare.services.DownloadService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainPageAfterLoggedIn extends AppCompatActivity implements SongClickListener {

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
            songAdapter = new SongAdapter(this,songs,this);
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

    @Override
    protected void onPause() {
        super.onPause();
        ArrayList<song> downloadedSongs= new ArrayList<>();
        for (int i = 0; i<songs.size(); i++){
            if (songs.get(i).getDownloaded()){
                downloadedSongs.add(songs.get(i));
                songItems.document(songs.get(i).idGet()).update("downloaded",true);
            }
        }
        if (!downloadedSongs.isEmpty()) downloadedToJSON(downloadedSongs);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSongsFromDB() {
        songs.clear();
        songItems.orderBy("title").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                song song1 = document.toObject(song.class);
                song1.setDocumentID(document.getId());
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

    private void downloadSong(String filename){
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("fileName",filename);
        startService(intent);
    }

    public void openUpload(View view) {
        Intent openUpload = new Intent(this, UploadSong.class);
        openUpload.putExtra("upload",1);
        startActivity(openUpload);
        finish();
    }

    @Override
    public void onSongClicked(String title) {
        songItems.document(title).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String filename = (String) task.getResult().get("fileName");
                int i=0;
                while (i<songs.size() && !songs.get(i).idGet().equals(title)){
                    i++;
                }
                songs.get(i).download();
                songAdapter.notifyDataSetChanged();
                downloadSong(filename);
            }
        });
    }

    public void downloadedToJSON(ArrayList<song> songs){
        new WriteToJSON().execute(songs);
    }

    private class WriteToJSON extends AsyncTask<ArrayList<song>,Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<song>... arrayLists) {
            ArrayList<song> downloadedSongs= new ArrayList<>();
            Gson gson = new Gson();
            for (int i = 0; i< arrayLists[0].size(); i++){
                if (arrayLists[0].get(i).getDownloaded()){
                    downloadedSongs.add(arrayLists[0].get(i));
                }
            }
            String jsonContent = gson.toJson(downloadedSongs);
            try {
                File appDir = new File(Environment.getExternalStorageDirectory()+"/Android/data/.zeneMegoszto");
                File JSONFile = new File(appDir, "downloaded.json");
                FileOutputStream fos = new FileOutputStream(JSONFile);
                try {
                    fos.write(jsonContent.getBytes());
                    fos.close();
                    Log.i(MainPageAfterLoggedIn.class.getName(), jsonContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}