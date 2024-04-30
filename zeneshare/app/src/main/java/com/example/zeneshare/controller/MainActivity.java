package com.example.zeneshare.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
        ETEmail=findViewById(R.id.EmailLogin);
        ETPassword=findViewById(R.id.passwordLogin);
        TVError=findViewById(R.id.error);
        if (ETEmail.getText().toString().isEmpty())
        {
            TVError.setText(R.string.empty_email);
        }
        else {
            if (ETPassword.getText().toString().isEmpty())
            {
                TVError.setText(R.string.empty_password);
            }
            else {
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

    public void openRegister(View view) {
        Intent register = new Intent(this,RegisterActivity.class);
        register.putExtra("registerType",1);
        startActivity(register);
    }
}