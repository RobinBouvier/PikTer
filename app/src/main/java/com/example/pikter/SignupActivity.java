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

    Button boutonInscription; //on déclare le bouton pour l'inscription
    public static class User { //on définit nos variables utiles
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
        setContentView(R.layout.inscription); //on affiche notre layout inscription

        //transformer en view
        boutonInscription = (Button) findViewById(R.id.signup_button);
        //on récupère les champs de saisie du XML
        final EditText prenom = findViewById(R.id.first_name);
        final EditText nom = findViewById(R.id.last_name);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);


        boutonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserDatabase(prenom, nom, email, password);
            }
            //si le bouton est cliqué on ajoute l'utilisateur a la bdd
        });
    }

    public void onLoginClicked(View view) {
        // créer un intent pour démarrer LoginActivity
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent); // Lancer l'activité login
    }


    private void addUserDatabase(EditText prenom, EditText nom, EditText email, EditText password) {
        //on récupère l'instance de la database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //on récupère la base de données user
        DatabaseReference myRef = database.getReference("user");

        //on détermine l'id de l'utilisateur : son mail en string
        String userId = email.getText().toString();

        //on crée l'utilisateur avec ses différents caractéristiques.
        User user = new User(prenom.getText().toString(), nom.getText().toString(), email.getText().toString(), password.getText().toString());

        //on l'ajoute à la base de donnée
        myRef.child(userId).setValue(user);

        // créer un intent pour démarrer MainActivity
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent); // lancer l'activité

    }
}