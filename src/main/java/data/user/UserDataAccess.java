package data.user;

import domain.entity.User;
import usecase.auth.UserDataAccessInterface;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class UserDataAccess implements UserDataAccessInterface {

    private final Map<String, User> usersByUsername = new HashMap<>();
    private final File storageFile;
    private long nextId = 1;

    public UserDataAccess(String filePath) {
        this.storageFile = new File(filePath);
        loadFromFile();
    }

    // ------------ load users from file on startup ------------
    private void loadFromFile() {
        if (!storageFile.exists()) {
            return; // no users yet
        }

        long maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Expected format: id,username,passwordHash,createdAtMillis
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                long id = Long.parseLong(parts[0]);
                String username = parts[1];
                String passwordHash = parts[2];
                long createdAtMillis = Long.parseLong(parts[3]);

                User user = new User(
                        id,
                        username,
                        passwordHash,
                        Instant.ofEpochMilli(createdAtMillis)
                );

                usersByUsername.put(username, user);
                if (id > maxId) {
                    maxId = id;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // for now just log
        }

        nextId = maxId + 1;
    }

    // ------------ write all users back to file ------------
    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(storageFile))) {
            for (User user : usersByUsername.values()) {
                pw.printf("%d,%s,%s,%d%n",
                        user.getId(),
                        user.getUsername(),
                        user.getPasswordHash(),
                        user.getCreatedAt().toEpochMilli());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------ Interface methods ------------

    @Override
    public synchronized boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }

    @Override
    public synchronized User getByUsername(String username) {
        return usersByUsername.get(username);
    }

    @Override
    public synchronized void save(User user) {
        usersByUsername.put(user.getUsername(), user);
        saveToFile();
    }

    @Override
    public synchronized long getNextId() {
        return nextId++;
    }
}
