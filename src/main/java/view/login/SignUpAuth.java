package view.login;
    import java.util.HashMap;
    import java.util.Map;
public class SignUpAuth {
    private static final Map<String, String> USERS = new HashMap<>();
    static {
        USERS.put("admin", "admin");
        USERS.put("user", "user");
    }
    public static boolean register(String username, String password) {
        if (USERS.containsKey(username)) {
            return false; // username taken
        }
        USERS.put(username, password);
        return true;
    }

    public static boolean authenticate(String username, String password) {
        String stored = USERS.get(username);
        return stored != null && stored.equals(password);
    }
}
