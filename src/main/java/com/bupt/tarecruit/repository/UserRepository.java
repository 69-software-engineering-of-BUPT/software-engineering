package com.bupt.tarecruit.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.JsonUtil;

public class UserRepository {
    private final Path dataDir;

    public UserRepository() {
        this(Paths.get("."));
    }

    public UserRepository(Path root) {
        this.dataDir = root.resolve("data").resolve("users");
    }

    public void saveUser(User user) throws IOException {
        JsonUtil.saveToJsonFile(user, dataDir.resolve("USER_" + user.getUserId() + ".json").toString());
    }

    public User getUserById(String userId) throws IOException {
        return JsonUtil.readFromJsonFile(dataDir.resolve("USER_" + userId + ".json").toString(), User.class);
    }

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<User>();
        java.io.File dir = dataDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return users;
        }
        java.io.File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (java.io.File file : files) {
                User user = JsonUtil.readFromJsonFile(file.getAbsolutePath(), User.class);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }
}
