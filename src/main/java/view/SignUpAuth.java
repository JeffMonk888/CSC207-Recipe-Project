package view;
    import java.util.HashMap;
    import java.util.Map;
public class SignUpAuth {
    private static final Map<String, String> USERS = new HashMap<>();
    static {
        USERS.put("admin", "admin");
        USERS.put("user", "user");
    }
    public static synchronized boolean register(String username, String password) {
        username = username.trim();
        if (username.isEmpty()) {
            return false;
        }
        if (USERS.containsKey(username)) {
            return false;          // username already exists
        }
        USERS.put(username, password);
        return true;
    }

    public static synchronized boolean authenticate(String username, String password) {
        username = username.trim();
        String stored = USERS.get(username);
        return stored != null && stored.equals(password);
    }
}
