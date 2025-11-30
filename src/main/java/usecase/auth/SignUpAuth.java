package usecase.auth;
import data.user.UserDataAccess;
import data.user.PasswordHash;
import domain.entity.User;
import usecase.auth.UserDataAccessInterface;
import usecase.auth.PasswordHasher;

import java.time.Instant;

public class SignUpAuth {

    // Shared backend objects
    private static final UserDataAccessInterface userDAO =
            new UserDataAccess("users.csv");      // file will be created if missing

    private static final PasswordHasher hasher = new PasswordHash();

    /** Register a new user. Returns true on success, false if username exists or invalid. */
    public static boolean register(String username, String password) {
        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        // username taken?
        if (userDAO.existsByUsername(username)) {
            return false;
        }

        long id = userDAO.getNextId();
        String passwordHash = hasher.hash(password);
        Instant createdAt = Instant.now();

        User user = new User(id, username, passwordHash, createdAt);
        userDAO.save(user);  // writes into users.csv

        return true;
    }
    public static User authenticateAndGetUser(String username, String password) {
        if (username == null || password == null ||
                username.isEmpty() || password.isEmpty()) {
            return null;
        }

        User user = userDAO.getByUsername(username);
        if (user == null) {
            return null;
        }

        String inputHash = hasher.hash(password);
        if (!inputHash.equals(user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    /** Check login credentials against stored users. */
    public static boolean authenticate(String username, String password) {
        username = username.trim();
        return authenticateAndGetUser(username, password) != null;
    }
}
