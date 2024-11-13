package com.example.pikter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    Button boutonInscription;
    public static class User {
        public String prenom;
        public String nom;
        public String email;
        public String password;

        public User(String prenom, String nom, String email, String password) {
            this.prenom = prenom;
            this.nom = nom;
            this.email = email;
            this.password = password;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        boutonInscription = (Button) findViewById(R.id.signup_button);

        final EditText prenom = findViewById(R.id.first_name);
        final EditText nom = findViewById(R.id.last_name);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);


        boutonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserDatabase(prenom, nom, email, password);
            }
        });
    }

    public void onLoginClicked(View view) {
        // Créer un intent pour démarrer LoginActivity
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent); // Lancer l'activité
    }


    private void addUserDatabase(EditText prenom, EditText nom, EditText email, EditText password) {
        //on récupère l'instance de la database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //on récupère la base de données user
        DatabaseReference myRef = database.getReference("user");

        //génère un id unique pour l'utilisateur
        String userId = myRef.push().getKey();

        //on crée l'utilisateur
        User user = new User(prenom.getText().toString(), nom.getText().toString(), email.getText().toString(), password.getText().toString());

        //on l'ajoute à la base de donnée
        myRef.child(userId).setValue(user);

        // Créer un intent pour démarrer MainActivity
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent); // Lancer l'activité

    }
}