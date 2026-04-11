package com.bupt.tarecruit.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> void saveToJsonFile(T object, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        }
    }

    public static <T> T readFromJsonFile(String filePath, Class<T> classOfT) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            return null;
        }
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, classOfT);
        }
    }
}