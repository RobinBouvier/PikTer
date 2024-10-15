package com.example.pikter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialogPost = null; //la fenêtre qui pop quand on appuie sur le bouton post
    Button post; //le bouton post en haut de la page
    LinearLayout postLayout; //ce layout est celui qui contient les posts

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
                        addPost(message.getText().toString());
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

    //permet d'ajouter un post à postsLayout
    private void addPost(String message){
        Date d = new Date(); //on récupère la date
        CharSequence date  = DateFormat.format("d MMMM, yyyy ", d.getTime()); //on la formate

        final View view = getLayoutInflater().inflate(R.layout.post, null); //on spécifie le fichier xml qui sert de view

        //on met la date dans le champ dateBody
        TextView dateView = view.findViewById(R.id.dateBody);
        dateView.setText(date);

        //on met le message de l'utilisateur dans messageBody
        TextView userView = view.findViewById(R.id.messageBody);
        userView.setText(message);


        postLayout.addView(view);// on ajoute la view à postLayout

        //on supprime et recrée un dialogPost pour n'avoir aucun texte d'écrit par défaut dans le champ message
        dialogPost.dismiss();
        buildDialog();
    }
}