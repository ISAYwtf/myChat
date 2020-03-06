package com.example.skillboxchat;

import com.google.gson.Gson;

public class Protocol {
    public static final int USER_STATUS = 1; // Сообщение о статусе (онлайн / оффлайн)
    public static final int MESSAGE = 2; // Сообщения (Входящие и исходящие)
    public static final int USER_NAME = 3; // Сообщаем свое имя серверу

    // Структура пользователя
    static class User {
        private long id; // Номер
        private String name; // Имя

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User() {}
    }

    // Структура сообщения о статусе пользователя
    static class UserStatus {
        private User user; // Пользователь
        private boolean connected; // Подключен ли

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public UserStatus() {}
    }

    static class Message {
        private static final int GROUP_CHAT = 1; // ID группового чата
        private long sender; // Отправитель
        private long receiver = GROUP_CHAT; // Получатель
        private String encodedText; // Текст сообщения

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }
    }

    static class UserName {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UserName(String name) {
            this.name = name;
        }
    }

    // Тип пришедшего сообщения
    public static int getType(String json) {
        if (json == null || json.length() == 0) {
            return -1;
        }
        return Integer.valueOf(json.substring(0, 1)); // Первый символ
    }

    // Преобразовывает Json  в Объект
    public static UserStatus unpackStatus(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

    public static Message unpackMessage(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), Message.class);
    }

    public static String packMessage(Message message) {
        Gson g = new Gson();
        return MESSAGE + g.toJson(message);
    }

    public static String packName(UserName name) {
        Gson g = new Gson();
        return USER_NAME + g.toJson(name);
    }

}
