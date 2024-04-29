package com.example.zeneshare.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.example.zeneshare.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class UploadService extends Service {

    private static final String TAG = "UploadService";
    private StorageReference storageRef;
    private static final String CHANNEL_ID = "UploadServiceChannel";
    @Override
    public void onCreate(){
        super.onCreate();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fileUri = intent.getStringExtra("fileUri");
        if (fileUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                startForeground(1, createNotification());
            }
            uploadFile(Uri.parse(fileUri));
        }
        return START_NOT_STICKY;
    }

    private void uploadFile(Uri fileUri) {
        StorageReference songsRef = storageRef.child("songs");
        String[] fileUriParts = fileUri.toString().split("/");
        StorageReference fileRef = songsRef.child(fileUriParts[fileUriParts.length-1]+".mp3");
        Log.i(TAG,fileRef.toString());
        File file = new File(String.valueOf(fileUri));
        if (!file.exists()) {
            Log.e(TAG, "File not found: " + fileUri);
        }
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();
        UploadTask uploadTask = fileRef.putFile(Uri.fromFile(file), metadata);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            long bytesTransferred = taskSnapshot.getBytesTransferred();
            long totalBytes = taskSnapshot.getTotalByteCount();
            int progress = (int) ((100.0 * bytesTransferred) / totalBytes);
            Log.i(TAG,""+progress);
            updateProgressNotification(progress);
        }).addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "Upload successful");
            stopForeground(true);
            stopSelf();
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Upload failed: " + exception.getMessage());
            stopForeground(true);
            stopSelf();
        });
    }

    private Notification createNotification() {
        createNotificationChannel();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Uploading...")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upload Channel";
            String description = "Channel for upload progress notification";
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateProgressNotification(int progress) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Uploading...")
                .setContentText(progress + "% completed")
                .setProgress(100, progress, false)
                .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1, notification);
    }

    private void cancelProgressNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(1);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}