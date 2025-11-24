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

    /** Check login credentials against stored users. */
    public static boolean authenticate(String username, String password) {
        username = username.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        User user = userDAO.getByUsername(username);
        if (user == null) {
            return false;
        }

        String inputHash = hasher.hash(password);
        return inputHash.equals(user.getPasswordHash());
    }
}
