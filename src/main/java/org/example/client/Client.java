package org.example.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.objects.GraphicObject;
import org.example.objects.ObjectList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements ClientListener {

    private Socket socket;
    private DataOutputStream outputStream; // для записи
    private DataInputStream inputStream; // для чтения
    private static final String HOST = "localhost";
    private static final int PORT = 12347;
    private Frame frame;
    private Thread thread;
    private ObjectList objectList;
    private XmlMapper xmlMapper;

    public static void main(String[] args) {
        Client client = new Client();
    }

    public Client() {
        objectList = new ObjectList();
        this.xmlMapper = new XmlMapper();
        frame = new Frame(this);
        createThread();

    }

    private void createThread() {
        thread = new Thread(() -> {
            try {

                socket = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());

                while (true) {
                    String inputString;
                    inputString = inputStream.readUTF();
                    if (inputString.equals("close")) {
                        close();
                    } else if (inputString.equals("updateList")) {
                        int size = inputStream.readInt();
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            list.add(inputStream.readUTF());
                        }
                        frame.updateList(list);
                    } else {
                        objectList.add(deserializeXMLObject(inputString));
                        frame.repaintDrawingPanel();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    @Override
    public void addButtonAction() {
        objectList.generateFigure();
        frame.repaintDrawingPanel();
    }

    @Override
    public List<GraphicObject> getObjectList() {
        return objectList.getObjects();
    }

    @Override
    public void clearButtonAction() {
        objectList.clear();
        frame.repaintDrawingPanel();
    }

    @Override
    public void closeButtonAction() {
        try {
            outputStream.writeUTF("close");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() {
        try {
            frame.dispose();
            inputStream.close();
            outputStream.close();
            socket.close();
            thread.interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendButtonAction(String name, GraphicObject object) {
        try {
            outputStream.writeUTF(name);
            outputStream.writeUTF(serializeXMLObject(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getButtonAction(String name) {
        try {
            outputStream.writeUTF("get");
            outputStream.writeUTF(name);
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

}
