package com.example.pikter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
    }

    public void onSignUpClicked(View view) {
        // Créer un intent pour démarrer SignupActivity
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent); // Lancer l'activité
    }
}
