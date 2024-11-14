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

    Button boutonLogin;
    public static String userConnecte;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("user");
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        //transformer les choses en view
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

    private void setUserConnecte(String userConnecteParam){
        userConnecte = userConnecteParam;
    }

    public static String getUserConnecte(){
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}






