package com.example.pikter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private List<String> displayedMessages = new ArrayList<>(); // on crée une liste avec l'ensemble des messages déjà affiché
    AlertDialog dialogPost = null; //la fenêtre qui pop quand on appuie sur le bouton post
    Button post; //le bouton post en haut de la page
    LinearLayout postLayout; //ce layout est celui qui contient les posts

    // Classe Post
    public static class Post {// on définit l'objet post avec toutes ses variables
        public String message;
        public String date;
        public String user;
        public int likes;
        public long timestamp;
        public String key;
        public List<String> listeUserLike;

        public Post() {
            // Constructeur par défaut requis pour Firebase
            this.listeUserLike = new ArrayList<>(); // Initialise la liste vide si non initialisée
        }

        public Post(String message, String date, String user, int likes) {
            this.message = message;
            this.date = date;
            this.user = user;
            this.likes = likes;
            this.timestamp = System.currentTimeMillis(); //prend la date
            this.key = null; //on s'en servira plus tard pour définir un id pour les post
            this.listeUserLike = new ArrayList<>();
        }

        public void ajoutLike(String userId) {
            if (!listeUserLike.contains(userId)) { //verifie dans la liste des utilisateur qui ont like ce post
                // Si l'utilisateur n'est pas dans la liste, on l'ajoute
                listeUserLike.add(userId);
                this.likes++; // On incrémente les likes
            }
        }

        public long tempsJours() {
            long temp = System.currentTimeMillis();
            return (temp - timestamp) / (1000 * 60 * 60 * 24); // convertit en jours
        }

        public double score() {
            long duree = tempsJours(); //récupère la date
            return (30 - duree) * likes; // formule pour le score
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //on initialise l'application pour firebase
        FirebaseApp.initializeApp(this);

        post = (Button) findViewById(R.id.postButton); //change le bouton post en view
        postLayout = findViewById(R.id.postsLayout); //récupère le postLayout

        buildDialog(); //on appelle la fonction buildDialog pour créer la pop-up

        //quand on clique sur le bouton post ça affiche la pop-up pour ajouter un post
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPost.show();
            }
        });
        fetchPostDatabase();
    }


    //créer la bulle qui permet d'ajouter un message
    public void buildDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);//on crée un builder de l'alert dialog qui va permettre de lui donner des paramètres
        View view = getLayoutInflater().inflate(R.layout.dialog_post, null);//permet de transformer un fichier XML en une view pour l'utiliser dans le code

        final EditText message = view.findViewById(R.id.postEdit); //on crée la variable qui va contenir le message

        builder.setView(view); //spécifie la view qui sera montré dans l'alert dialog
        builder.setTitle("Écrivez votre post") //on met un titre
                //on crée un bouton qui sert de positive (on valide ce qu'on fait)
                .setPositiveButton("Poster", new DialogInterface.OnClickListener() {
                    @Override
                    //qua
                    public void onClick(DialogInterface dialog, int which) {
                        addPostDatabase(message.getText().toString()); //on ajoute les post a la bdd
                        dialogPost.dismiss(); //on ferme la fenetre pop up
                        buildDialog(); //on la reset
                    }
                })
                //on crée un bouton qui sert de negative (on annule ce qu'on fait)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialogPost = builder.create(); //on met ce builder dans la view dialogPost
    }



    private void addPostDatabase(String message) {
        //on récupère l'instance de la database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //on récupère la base de données posts
        DatabaseReference myRef = database.getReference("posts");

        //génère un id unique pour le post
        String postId = myRef.push().getKey();

        Date d = new Date(); //on récupère la date
        CharSequence dateChar = DateFormat.format("d MMMM, yyyy ", d.getTime()); //on la formate
        String date = dateChar.toString();

        //on crée le post
        Post post = new Post(message, date, LoginActivity.getUserConnecte(), 0);

        //on l'ajoute à la base de donnée
        myRef.child(postId).setValue(post);
    }
    //récupère les posts dans la base de données
    //on change le nombre de like dans la bdd en ajoutant aussi les utilisateurs qui ont like
    private void updateLikesInDatabase(String postId, int likes, List<String> listeUserLike) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("posts").child(postId);
        myRef.child("likes").setValue(likes);
        myRef.child("listeUserLike").setValue(listeUserLike);
    }


    private void fetchPostDatabase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>(); // Liste pour stocker les posts

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.key = postSnapshot.getKey(); // Stocker la clé du post
                        posts.add(post); // Ajouter chaque post à la liste
                    }
                }


                // Compte les posts
                int numberOfPosts = posts.size();

                //afficher le compteur
                TextView decoTextView = findViewById(R.id.nbrMessages);
                decoTextView.setText("Posts : " + numberOfPosts + "   ");

                // Trier les posts par score
                posts.sort((p1, p2) -> Double.compare(p2.score(), p1.score()));

                // Afficher les posts triés
                postLayout.removeAllViews(); // Vider le layout avant d'ajouter les posts triés
                for (Post post : posts) {
                    displayPost(post); // Appeler displayPost sans passer la clé ici
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération des données", error.toException());
            }
        });
    }
    private void nbrPost(Post post) {

    }


    // Méthode pour afficher un post
    private void displayPost(Post post) {
        final View view = getLayoutInflater().inflate(R.layout.post, null); // on crée une vue à partir du layout de chaque post

        String userId = LoginActivity.getUserConnecte(); // on récupère l'utilisateur connecté

        // on met à jour la date dans la vue
        TextView dateView = view.findViewById(R.id.dateBody);
        dateView.setText(post.date);

        // on met à jour le nom de l'utilisateur qui a posté
        TextView userView = view.findViewById(R.id.userBody);
        userView.setText(post.user);

        // on met à jour le contenu du message
        TextView messageView = view.findViewById(R.id.messageBody);
        messageView.setText(post.message);

        // on gère les likes avec un bouton toggle
        ToggleButton likeButton = view.findViewById(R.id.likeButton);
        TextView likesCountView = view.findViewById(R.id.nbrLike);
        likesCountView.setText(post.likes + " Likes"); // on affiche le nombre de likes
        if(post.listeUserLike.contains(userId)) { // si l'utilisateur a déjà liké, on coche le bouton
            likeButton.setChecked(true);
        }
        else { // sinon, on laisse le bouton non coché
            likeButton.setChecked(false);
        }

        //gère ce que fais le like
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.listeUserLike.contains(userId)) {
                    // Si l'utilisateur a déjà liké, on enlève le like
                    post.listeUserLike.remove(userId);
                    post.likes--; // Décrémenter les likes
                    ((ToggleButton)v).setChecked(false);

                } else {
                    // Si l'utilisateur n'a pas encore liké, on ajoute le like
                    post.listeUserLike.add(userId);
                    post.likes++; // Incrémenter les likes
                    ((ToggleButton)v).setChecked(true);

                }
                likesCountView.setText(post.likes + " Likes");
                updateLikesInDatabase(post.key, post.likes, post.listeUserLike); // Utiliser post.key ici
            }
        });

        postLayout.addView(view); // Ajout de la vue au layout
    }
}
