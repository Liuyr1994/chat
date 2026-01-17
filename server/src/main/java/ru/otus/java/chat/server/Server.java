package ru.otus.java.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;


    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
