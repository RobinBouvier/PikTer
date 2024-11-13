package com.example.pikter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button boutonLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        boutonLogin = (Button) findViewById(R.id.login_button);

        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);

        boutonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(email, password);
            }
        });
    }



    public void onSignUpClicked(View view) {
        // Créer un intent pour démarrer SignupActivity
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent); // Lancer l'activité

    }

    public void login(EditText email, EditText password){


    }

}
