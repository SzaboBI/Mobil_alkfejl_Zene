package com.example.zeneshare.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zeneshare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public EditText ETEmail;
    public EditText ETPassword;
    public TextView TVError;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        TVError = findViewById(R.id.error);
        TVError.setText(R.string.empty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login(View view) {
        int error =0;
        ETEmail=findViewById(R.id.EmailLogin);
        ETPassword=findViewById(R.id.passwordLogin);
        TVError=findViewById(R.id.error);
        if (ETEmail.getText().toString().isEmpty())
        {
            TVError.setText(R.string.empty_email);
            error++;
        }
        else {
            if (ETPassword.getText().toString().isEmpty())
            {
                TVError.setText(R.string.empty_password);
                error++;
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        requestPermissions();
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED){
                            TVError.setText(R.string.permissiondenied);
                            error++;
                        }
                }
                else {
                        requestPermissions();
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ){
                            TVError.setText(R.string.permissiondenied);
                            error++;
                        }

                }
                if (error==0){
                    Intent intent = new Intent(this, MainPageAfterLoggedIn.class);
                    mAuth.signInWithEmailAndPassword(ETEmail.getText().toString(),ETPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        startActivity(intent);
                                    }
                                    else {
                                        TVError.setText(R.string.unsuccessful_login);
                                    }
                                }
                            });
                }
            }
        }


    }

    private void requestPermissions() {
        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {
                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        1);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ) {
                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // Check if the permission is granted
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can proceed with your operations
            } else {
                requestPermissions();
            }
        }
    }

    public void openRegister(View view) {
        Intent register = new Intent(this,RegisterActivity.class);
        register.putExtra("registerType",1);
        startActivity(register);
    }
}