package com.bupt.tarecruit.repository;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String DATA_DIR = "data/users/";

    public void saveUser(User user) throws IOException {
        String filePath = DATA_DIR + "USER_" + user.getUserId() + ".json";
        JsonUtil.saveToJsonFile(user, filePath);
    }

    public User getUserById(String userId) throws IOException {
        String filePath = DATA_DIR + "USER_" + userId + ".json";
        return JsonUtil.readFromJsonFile(filePath, User.class);
    }

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    User user = JsonUtil.readFromJsonFile(file.getAbsolutePath(), User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        }
        return users;
    }
}