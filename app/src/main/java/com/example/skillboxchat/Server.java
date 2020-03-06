package com.example.skillboxchat;

import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    WebSocketClient client;

    // Нам MainActivity - это потребитель сообщений
    // Пара из двух строк - имя пользователя и текст сообщений
    private Consumer<Pair<String, String>> messageConsumer;

    // Карта имен пользователей
    private Map<Long, String> nameMap = new ConcurrentHashMap<>();

    public Server(Consumer<Pair<String, String>> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    // Будем вызывать, когда нужно подключиться к серверу
    public void connect() {
        URI addr;
        try {
            addr = new URI("ws://35.210.129.230:8881/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        client = new WebSocketClient(addr) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                // При подключении
                Log.i("WSSERVER", "Connected to server");
            }

            @Override
            public void onMessage(String json) {
                // При получении сообщения с сервера
                int type = Protocol.getType(json);
                if (type == Protocol.MESSAGE) {
                    onIncomingTextMessage(json);
                }
                if (type == Protocol.USER_STATUS) {
                    // Обновить статус пользователя
                    onStatusUpdate(json);
                }
                Log.i("WSSERVER", "Received message: " + json);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                // При отключении
                Log.i("WSSERVER", "Connection closed");
            }

            @Override
            public void onError(Exception ex) {
                // При возникновении ошибки
                Log.i("WSSERVER", "Error occured: " + ex.getMessage() );
            }
        };
        client.connect();
    }

    private void onStatusUpdate(String json) {
        Protocol.UserStatus status = Protocol.unpackStatus(json);
        Protocol.User u = status.getUser();
        if (status.isConnected()) { // Новый пользователь подключился
            nameMap.put(u.getId(), u.getName()); // Положить имя в "карту"
        } else { // Пользователь отключился
            nameMap.remove(u.getId());
        }
    }

    private void onIncomingTextMessage(String json) {
        Protocol.Message message = Protocol.unpackMessage(json);
        String name = nameMap.get(message.getSender() );
        if (name == null) {
            name = "Unnamed";
        }
        messageConsumer.accept(new Pair<String, String>(message.getEncodedText(), name) );
    }

    public void sendMessage(String messageText) {
        String json = Protocol.packMessage(
                new Protocol.Message(messageText)
        );
        if (client != null && client.isOpen() ) {
            client.send(json);
        }
    }

    public void sendName(String name) {
        String json = Protocol.packName(
                new Protocol.UserName(name)
        );
        if (client != null && client.isOpen() ) {
            client.send(json);
        }
    }

}