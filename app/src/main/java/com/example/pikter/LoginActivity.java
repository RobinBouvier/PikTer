package com.example.pikter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button boutonLogin; //on déclare le boutton pour se connecter
    public static String userConnecte;
    FirebaseDatabase database = FirebaseDatabase.getInstance(); // on déclare la bdd
    DatabaseReference usersRef = database.getReference("user"); // on accède aux users dans notre bdd
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // plein ecran
        setContentView(R.layout.login); //on affiche notre layout login


        boutonLogin = (Button) findViewById(R.id.login_button); //on récupère le boutton du xml
//final pour pas que la variable soit réasignée
        final EditText email = findViewById(R.id.email);  //on récupère les champs de saisie du XML
        final EditText password = findViewById(R.id.password); //on récupère les champs de saisie du XML

        boutonLogin.setOnClickListener(new View.OnClickListener() { //vérifie si le boutton est cliqué
            @Override
            public void onClick(View view) {
                login(email, password);
            }  //si le bouton est cliqué on utilise la fonction login avec le mail et le mdp
        });
    }

    private void setUserConnecte(String userConnecteParam){ //permet de savoir quel est l'utilisateur connecté actuellement
        userConnecte = userConnecteParam;
    }

    public static String getUserConnecte(){//permet de récupérer le nom de l'utilisateur en string
        return userConnecte;
    }


    public void onSignUpClicked(View view) {
        // Créer un intent pour démarrer SignupActivity
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent); // Lancer l'activité
    }

    public void login(EditText email, EditText password) {
        // On récupère la référence à la base de données avec l'email de l'utilisateur comme clé
        // L'email est utilisé comme clé pour rechercher l'utilisateur dans la base de données.
        usersRef.child(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Vérifie si l'utilisateur existe dans la base de données
                if (dataSnapshot.exists()) {
                    // Récupère le mot de passe stocké dans la base de données
                    String storedPassword = dataSnapshot.child("password").getValue(String.class);

                    // Compare le mot de passe saisi avec celui stocké dans la base de données
                    if (storedPassword != null && storedPassword.equals(password.getText().toString())) {

                        // Si le mot de passe est correct
                        Log.d("Firebase", "L'utilisateur avec ID " + email.getText().toString() + " est connecté.");

                        // Crée un Intent pour démarrer l'activité MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);  // Lance l'activité Main

                        // Sauvegarde l'email de l'utilisateur connecté
                        setUserConnecte(email.getText().toString());

                    } else {
                        // Si le mot de passe ne correspond pas
                        Log.d("Firebase", "Mot de passe incorrect pour l'utilisateur " + email.getText().toString());
                    }
                } else {
                    // Si l'utilisateur n'existe pas dans la base de données
                    Log.d("Firebase", "L'utilisateur avec ID " + email.getText().toString() + " n'existe pas.");
                }
            }

            @Override // gestion des erreur (sinon le programme plante)
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}






