package com.example.zeneshare.adapter;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AudioPickerContract extends ActivityResultContract<String, Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(s);
        return intent;
    }

    @Override
    public Uri parseResult(int i, @Nullable Intent intent) {
        if (intent == null || i != RESULT_OK) {
            return null;
        }
        Log.i("AudioPickerContract", Environment.getExternalStorageDirectory().getAbsolutePath());
        return intent.getData();
    }
}
