package com.bupt.tarecruit.repository;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists user accounts as JSON files under the local {@code data/users}
 * directory. Login and registration both rely on this repository for account
 * lookup and storage.
 */
public class UserRepository {
    private static final String DATA_DIR = "data/users/";

    /**
     * Writes a user record to its canonical JSON file.
     *
     * @param user user entity to persist
     * @throws IOException when the file cannot be written
     */
    public void saveUser(User user) throws IOException {
        String filePath = DATA_DIR + "USER_" + user.getUserId() + ".json";
        JsonUtil.saveToJsonFile(user, filePath);
    }

    /**
     * Removes a persisted user record by its user ID.
     *
     * @param userId identifier of the account to delete
     * @throws IOException when the user file exists but cannot be removed
     */
    public void deleteUser(String userId) throws IOException {
        File file = new File(DATA_DIR + "USER_" + userId + ".json");
        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete user file: " + file.getPath());
        }
    }

    /**
     * Loads a single user record by ID.
     *
     * @param userId identifier used in the login or registration flow
     * @return deserialised user record, or {@code null} when no file exists
     * @throws IOException when the JSON file cannot be read
     */
    public User getUserById(String userId) throws IOException {
        String filePath = DATA_DIR + "USER_" + userId + ".json";
        return JsonUtil.readFromJsonFile(filePath, User.class);
    }

    /**
     * Loads every stored user account from the repository directory.
     *
     * @return list of all successfully parsed user records
     * @throws IOException when reading a user file fails
     */
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("USER_") && name.endsWith(".json"));
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
