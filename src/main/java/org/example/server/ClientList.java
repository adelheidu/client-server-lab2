package org.example.server;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ClientList {

    private static final List<ServerConnection> connectionList = new ArrayList<>();

    public static synchronized void addClient(ServerConnection serverConnection) {
        connectionList.add(serverConnection);
    }

    public static synchronized void removeClient(ServerConnection serverConnection) {
        connectionList.remove(serverConnection);
    }

    public static void notifyClients() {
        connectionList.forEach(ServerConnection::notifyClient);
    }

}
