package com.example.zeneshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register2 extends AppCompatActivity {

    public EditText ETBirthDate;
    public EditText ETPhoneNumber;
    public TextView TVError;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.getIntExtra("registerType",1)==1)
        {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_register2);
            TVError = findViewById(R.id.error);
            TVError.setText(R.string.empty);
            mAuth= FirebaseAuth.getInstance();
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        else {
            finish();
            Intent openLogin = new Intent(this, MainActivity.class);
            startActivity(openLogin);
        }
    }

    public void closeRegister(View view) {
        finish();
        Intent openLogin = new Intent(this, MainActivity.class);
        startActivity(openLogin);
    }

    public void register(View view) {
        ETBirthDate= findViewById(R.id.birthdate);
        ETPhoneNumber=findViewById(R.id.telefonszam);
        TVError=findViewById(R.id.error);
        Intent intent = getIntent();
        String email=intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        Intent openLogin = new Intent(this,MainActivity.class);
        Log.i(register2.class.getName(),email);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Log.d(register2.class.getName(),"Sikeres regisztracio!");
                    openLogin.putExtra("email",email);
                    startActivity(openLogin);
                }
                else {
                    Log.e(register2.class.getName(),"Sikertelen regisztracio! "+task.getException().getMessage()+".");
                    TVError.setText(R.string.unsuccesful_registration);
                }
            }
        });
    }
}