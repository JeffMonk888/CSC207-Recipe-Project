package data.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import domain.entity.User;
import usecase.auth.UserDataAccessInterface;

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
        if (storageFile.exists()) {

            long maxId = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    // Expected format: id,username,passwordHash,createdAtMillis
                    final String[] parts = line.split(",", -1);
                    if (parts.length < 4) {
                        continue;
                    }

                    final long id = Long.parseLong(parts[0]);
                    final String username = parts[1];
                    final String passwordHash = parts[2];
                    final long createdAtMillis = Long.parseLong(parts[3]);

                    final User user = new User(
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
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }

            nextId = maxId + 1;
        }
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
        }
        catch (IOException ex) {
            ex.printStackTrace();
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
