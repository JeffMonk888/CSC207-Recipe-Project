package usecase.auth;

public interface PasswordHasher {
    String hash(String rawPassword);
}
