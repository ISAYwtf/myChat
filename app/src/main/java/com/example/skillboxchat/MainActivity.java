package com.example.skillboxchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.cryptochat.MessageController;

public class MainActivity extends AppCompatActivity {

    Server server;
    MessageController controller;
    String myName;

    @Override
    protected void onStart() {
        super.onStart();
        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Сюда приходят сообщения
                        controller.addMessage(
                            new MessageController.Message(
                                pair.first,
                                pair.second,
                                false
                            )
                        );
                    }
                });
            }
        });
        server.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Не забыть отключиться
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Вызывается при создании окошка (Activity)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Интерфейс этого окошка лежит в activity_main

        final EditText chatMessage = findViewById(R.id.chatMessage);
        ImageButton sendButton = findViewById(R.id.sendButton);
        RecyclerView chatWindow = findViewById(R.id.chatWindow);

        controller = new MessageController();

        controller.setIncomingLayout(R.layout.message)
                .setOutgoingLayout(R.layout.outgoing_message)
                .setMessageTextId(R.id.messageText)
                .setUserNameId(R.id.userName)
                .setMessageTimeId(R.id.messageTime)
                .appendTo(chatWindow, this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = chatMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(
                                userMessage,
                                myName,
                                true
                        )
                );
                server.sendMessage(userMessage);
                chatMessage.setText("");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name:");

        final EditText nameInput = new EditText(this);
        builder.setView(nameInput);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = nameInput.getText().toString();
                server.sendName(myName);
            }
        });

        builder.show();
    }
}