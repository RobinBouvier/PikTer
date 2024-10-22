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

    AlertDialog dialogPost = null; //la fenêtre qui pop quand on appuie sur le bouton post
    Button post; //le bouton post en haut de la page
    LinearLayout postLayout; //ce layout est celui qui contient les posts

    // Classe Post
    public static class Post {
        public String message;
        public String date;
        public String user;
        public int likes;

        public Post() {
            // Constructeur par défaut requis pour Firebase
        }

        public Post(String message, String date, String user, int likes) {
            this.message = message;
            this.date = date;
            this.user = user;
            this.likes = likes;
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

    //gère ce que fais le like
    public void onLikeToggleClick(View view) {

    }

    //créer la bulle qui permet d'ajouter un message
    public void buildDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);//on crée un builder de l'alert dialog qui va permettre de lui donner des paramètres
        View view = getLayoutInflater().inflate(R.layout.dialog_post, null);//permet de transformer un fichier XML en une view pour l'utiliser dans le code

        final EditText message = view.findViewById(R.id.postEdit); //on crée la variable qui va contenir le message

        builder.setView(view); //spécifie la view qui sera montré dans l'alert dialog
        builder.setTitle("Écrivez votre post") //on met un titre
                //on crée un bouton qui sert de positive (on valide ce qu'on fait)
                .setPositiveButton("Poster", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        addPostDatabase(message.getText().toString());
                        dialogPost.dismiss();
                        buildDialog();
                    }
                })
                //on crée un bouton qui sert de negative (on annule ce qu'on fait)
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick (DialogInterface dialog,int which){

                    }
                });

        dialogPost = builder.create(); //on met ce builder dans la view dialogPost
    }

    /**
    //permet d'ajouter un post à postsLayout
    private void addPost(String message){
        //postLayout.addView(view);// on ajoute la view à postLayout
        addPostDatabase(message);//on appelle la fonction pour ajouter le message à la BD

        //on supprime et recrée un dialogPost pour n'avoir aucun texte d'écrit par défaut dans le champ message
        dialogPost.dismiss();
        buildDialog();
    }**/

    private void addPostDatabase(String message){
        //on récupère l'instance de la database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //on récupère la base de données posts
        DatabaseReference myRef = database.getReference("posts");

        //génère un id unique pour le post
        String postId = myRef.push().getKey();

        Date d = new Date(); //on récupère la date
        CharSequence dateChar  = DateFormat.format("d MMMM, yyyy ", d.getTime()); //on la formate
        String date = dateChar.toString();

        //on crée le post
        Post post = new Post(message, date, "Alice", 10);

        //on l'ajoute à la base de donnée
        myRef.child(postId).setValue(post);
    }

    //récupère les posts dans la base de données
    private void fetchPostDatabase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Cette méthode est appelée lorsque les données changent
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Déclaration des variables
                    String message;
                    String date;
                    String user;
                    int likes;

                    Post post = postSnapshot.getValue(Post.class);
                    message = post.message;
                    date = post.date;
                    user = post.user;
                    likes = post.likes;

                    final View view = getLayoutInflater().inflate(R.layout.post, null); //on spécifie le fichier xml qui sert de view

                    //on met la date dans le champ dateBody
                    TextView dateView = view.findViewById(R.id.dateBody);
                    dateView.setText(date);

                    //on met la user dans le champ userBody
                    TextView userView = view.findViewById(R.id.userBody);
                    userView.setText(user);

                    //on met le message de l'utilisateur dans messageBody
                    TextView messageView = view.findViewById(R.id.messageBody);
                    messageView.setText(message);


                    /**
                    //on met les likes dans nbrLike
                    String likeString = String.valueOf(likes);
                    TextView likeView = view.findViewById(R.id.nbrLike);
                    likeView.setText(likes);
                    **/
                    postLayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération des données", error.toException());
            }
        });
    }
}