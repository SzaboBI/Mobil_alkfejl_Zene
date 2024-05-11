package com.example.zeneshare.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

public class DownloadService extends Service{

    private static final String TAG = "DownloadService";

    private NotificationHadler notificationHadler;

    private FirebaseFirestore songDb;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHadler = new NotificationHadler(this, "DownloadServiceChannel", getSystemService(NotificationManager.class), NotificationHadler.ACTIONS.DOWNLOAD);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fileName = intent.getStringExtra("fileName");
        songDb = FirebaseFirestore.getInstance();

        startForeground(1, notificationHadler.createNotification());

        downloadFile(fileName);


        return START_NOT_STICKY;
    }

    private void downloadFile(String fileName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("songs").child(fileName);
        Log.i(TAG, storageRef.toString());

        File directory = new File(Environment.getExternalStorageDirectory()+"/Download", "zeneMegoszto");
        if (!directory.exists()){
            directory.mkdir();
        }
        final File[] localFile = {new File(directory, fileName)};
        storageRef.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                switch (task.getResult().getContentType()){
                    case "audio/mpeg":
                        localFile[0].renameTo(new File(directory, fileName+".mp3"));
                        break;
                    case "audio/mp4":
                        localFile[0].renameTo(new File(directory, fileName+".m4a"));
                        break;
                    case "audio/ogg":
                        localFile[0].renameTo(new File(directory, fileName+".ogg"));
                        break;
                    case "audio/mid":
                        localFile[0].renameTo(new File(directory, fileName+".mid"));
                        break;
                    case "audio/x-aiff":
                        localFile[0].renameTo(new File(directory, fileName+".aif"));
                        break;
                    case "audio/vnd.wav":
                        localFile[0].renameTo(new File(directory, fileName+".wav"));
                        break;
                    case "audio/vnd.rn-realaudio":
                        localFile[0].renameTo(new File(directory, fileName+".ram"));
                        break;
                    default:
                        localFile[0].renameTo(new File(directory, fileName+".au"));
                        break;
                }
            }
        });

        storageRef.getFile(localFile[0]).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            notificationHadler.updateProgressNotification((int) progress);
        }).continueWithTask(new Continuation<FileDownloadTask.TaskSnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Download successful, show download complete notification
                //showDownloadCompleteNotification(localFile);

                return null;
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error downloading file: " + e.getMessage());
        }).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "Successful download!");
                notificationHadler.successfulEnd();
                songDb.collection("Songs").whereEqualTo("fileName", fileName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String id = task.getResult().getDocuments().get(0).getId();
                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put("downloaded",true);
                        songDb.collection("Songs").document(id).update(data);
                        Intent broadcastIntent = new Intent("com.example.zeneshare.ACTION_SERVICE_FINISHED");
                        sendBroadcast(broadcastIntent);
                    }
                });
            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
