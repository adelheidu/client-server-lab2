package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.objects.GraphicObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Thread {

    private Socket socket;
    private DataOutputStream outputStream; // для записи
    private DataInputStream inputStream; // для чтения
    private XmlMapper xmlMapper;

    public ServerConnection(Socket socket) {
        this.xmlMapper = new XmlMapper();
        this.socket = socket;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String inputString = inputStream.readUTF();
                if (inputString.equals("close")) {
                    outputStream.writeUTF("close");
                    inputStream.close();
                    outputStream.close();
                    ClientList.removeClient(this);
                } else if (inputString.equals("get")) {
                    GraphicObject object = ServerObjectList.getObjectByKey(inputStream.readUTF());
                    outputStream.writeUTF(serializeXMLObject(object));
                } else {
                    String name = inputString;
                    GraphicObject object = deserializeXMLObject(inputStream.readUTF());
                    ServerObjectList.addObject(name, object);
                    ClientList.notifyClients();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String serializeXMLObject(GraphicObject object) {
        String xmlObject;
        try {
            xmlObject = xmlMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(xmlObject);
        return xmlObject;
    }

    private GraphicObject deserializeXMLObject(String xmlObject) {
        GraphicObject object;
        try {
            object = xmlMapper.readValue(xmlObject, GraphicObject.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public void notifyClient() {
        try {
            outputStream.writeUTF("updateList");
            outputStream.writeInt(ServerObjectList.getNames().size());
            ServerObjectList.getNames().forEach(key -> {
                try {
                    outputStream.writeUTF(key);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
