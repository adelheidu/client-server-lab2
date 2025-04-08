package org.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private DataOutputStream outputStream; // для записи
    DataInputStream inputStream; // для чтения

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                serverSocket.accept();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}