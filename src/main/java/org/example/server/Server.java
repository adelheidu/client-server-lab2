package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 12347;

    public static void main(String[] args) {
        System.out.println("Server is running!");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ServerConnection serverConnection = new ServerConnection(socket);
                serverConnection.start();
                ClientList.addClient(serverConnection);
                serverConnection.notifyClient();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}