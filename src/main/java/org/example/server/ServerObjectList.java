package org.example.server;

import org.example.objects.GraphicObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerObjectList {

    private static final Map<String, GraphicObject> objects = new HashMap<>();

    public static synchronized void addObject(String name, GraphicObject object) {
        objects.put(name, object);
    }

    public static synchronized Set<String> getNames() {
        return objects.keySet();
    }

    public static synchronized GraphicObject getObjectByKey(String name) {
        return objects.get(name);
    }

}
