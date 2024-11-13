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

                    // Si l'utilisateur existe, on affiche un message dans les logs avec l'email de l'utilisateur
                    Log.d("Firebase", "L'utilisateur avec ID " + email + " existe.");

                    // Crée un Intent pour démarrer l'activité MainActivity
                    // l'utilisateur est connecté avec succès et est redirigé vers la page d'accueil
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);  // Lance l'activité Main

                    // Appelle la méthode setUserConnecte pour sauvegarder l'email de l'utilisateur connecté
                    setUserConnecte(email.getText().toString());

                } else {
                    // Si l'utilisateur n'existe pas dans la base de données, on affiche un message dans les logs
                    // avec l'email pour indiquer qu'il n'est pas trouvé.
                    Log.d("Firebase", "L'utilisateur avec ID " + email + " n'existe pas.");
                }
            }

            // Cette méthode est appelée si la lecture de la base de données est annulée pour une raison quelconque
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // En cas d'erreur, affiche un message de log pour indiquer qu'il y a eu un problème avec la lecture des données
                Log.w("Firebase", "Erreur de lecture : ");
            }
        });
    }

}






