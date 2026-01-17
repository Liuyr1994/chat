package ru.otus.java.chat.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket("localhost", 8189);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
        ) {
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        System.out.println("message = " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                String message = scanner.nextLine();
                out.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}