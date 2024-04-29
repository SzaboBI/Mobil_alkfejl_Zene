package com.example.zeneshare.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils {

    public static String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Log.i("FileUtils",MediaStore.Audio.Media.DATA);
        ContentResolver contentResolver = context.getContentResolver();

        // Query MediaStore to get the file path based on the document ID
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }

        return null; // File path not found
    }
}