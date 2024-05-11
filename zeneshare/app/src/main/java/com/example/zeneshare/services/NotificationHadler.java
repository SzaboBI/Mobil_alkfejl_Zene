package com.example.zeneshare.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.zeneshare.R;

public class NotificationHadler {

    private Context mContext;
    private final String CHANNEL_ID;
    private NotificationManager notificationManager;
    private ACTIONS action;

    public enum ACTIONS{
        UPLOAD,
        DOWNLOAD
    }

    public NotificationHadler(Context mContext, String CHANNEL_ID, NotificationManager nManager, ACTIONS action){
        this.mContext = mContext;
        this.CHANNEL_ID = CHANNEL_ID;
        this.notificationManager = nManager;
        this.action = action;
    }
    public Notification createNotification() {
        createNotificationChannel();

        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Uploading...")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upload Channel";
            String description = "Channel for upload progress notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
    }
    public void updateProgressNotification(int progress) {
        Notification notification = null;
        switch (this.action){
            case UPLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Feltoltes")
                        .setContentText(progress + "% feltoltve")
                        .setProgress(100, progress, false)
                        .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                        .build();
                break;
            case DOWNLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Letoltes")
                        .setContentText(progress + "% letoltve")
                        .setProgress(100, progress, false)
                        .setSmallIcon(R.drawable.downloadbuttonimage)
                        .build();
                break;
        }
        notificationManager.notify(1, notification);
    }

    public void successfulEnd(){
        Notification notification = null;
        switch (this.action){
            case UPLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Feltoltes")
                        .setContentText("Feltoltes kesz!")
                        .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                        .build();
                break;
            case DOWNLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Letoltes")
                        .setContentText("Letoltes kesz!")
                        .setSmallIcon(R.drawable.downloadbuttonimage)
                        .build();
                break;
        }
        notificationManager.notify(1, notification);
    }

    public void unSuccessfulEnd(){
        Notification notification = null;
        switch (this.action){
            case UPLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Feltoltes")
                        .setContentText("Feltoltes sikertelen!")
                        .setSmallIcon(R.drawable.baseline_arrow_upward_24)
                        .build();
                break;
            case DOWNLOAD:
                notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setContentTitle("Letoltes")
                        .setContentText("Letoltes sikertelen!")
                        .setSmallIcon(R.drawable.downloadbuttonimage)
                        .build();
                break;
        }
        notificationManager.notify(1, notification);
    }

    public void cancelProgressNotification() {
        notificationManager.cancel(1);
    }
}
