package ru.otus.java.chat.client;


import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}