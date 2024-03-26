package com.example.zeneshare.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zeneshare.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public EditText ETEmail;
    public EditText ETPassword;
    public EditText ETPasswordAgain;
    public TextView TVError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.getIntExtra("registerType",1)==1){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_register);
            TVError= findViewById(R.id.error);
            TVError.setText(R.string.empty);
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

    public void nextRegister(View view) {
        ETEmail=findViewById(R.id.registerEmailAddress);
        ETPassword=findViewById(R.id.registerPasswd);
        ETPasswordAgain=findViewById(R.id.registerPasswdAgain);
        TVError = findViewById(R.id.error);
        TVError.setText(R.string.empty);

        if (!ETEmail.getText().toString().isEmpty())
        {
            if (!ETPassword.getText().toString().isEmpty()){
                if (ETPassword.getText().toString().equals(ETPasswordAgain.getText().toString()))
                {
                    Pattern pattern=Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
                    Matcher matcher=pattern.matcher(ETEmail.getText().toString());
                    if (matcher.find())
                    {
                        finish();
                        Intent stepToNext = new Intent(this, register2.class);
                        stepToNext.putExtra("email",ETEmail.getText().toString());
                        stepToNext.putExtra("password",ETPassword.getText().toString());
                        stepToNext.putExtra("registerType",1);
                        startActivity(stepToNext);

                    }
                    else {
                        TVError.setText(R.string.incorrect_email);
                    }
                }
                else {
                    TVError.setText(R.string.different_password);
                }
            }
            else {
                TVError.setText(R.string.empty_password);
            }
        }
        else {
            TVError.setText(R.string.empty_email);
        }

    }
}