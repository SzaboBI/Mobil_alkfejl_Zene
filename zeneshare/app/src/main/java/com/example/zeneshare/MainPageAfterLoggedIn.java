package com.example.zeneshare;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPageAfterLoggedIn extends AppCompatActivity {

    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main_page_after_logged_in);
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
}