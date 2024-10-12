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

    AlertDialog dialogPost = null;
    Button post;
    LinearLayout postLayout;

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

        post = (Button) findViewById(R.id.postButton);
        postLayout = findViewById(R.id.postsLayout);

        buildDialog();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_post, null);

        final EditText message = view.findViewById(R.id.postEdit);

        builder.setView(view);
        builder.setTitle("Écrivez votre post")
                .setPositiveButton("Poster", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        addPost(message.getText().toString());
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick (DialogInterface dialog,int which){

                    }
                });

        dialogPost = builder.create();
    }

    //permet d'ajouter un post à postsLayout
    private void addPost(String message){
        Date d = new Date();
        CharSequence date  = DateFormat.format("d MMMM, yyyy ", d.getTime());

        final View view = getLayoutInflater().inflate(R.layout.post, null);

        TextView dateView = view.findViewById(R.id.dateBody);
        dateView.setText(date);

        TextView userView = view.findViewById(R.id.messageBody);
        userView.setText(message);

        postLayout.addView(view);
    }
}