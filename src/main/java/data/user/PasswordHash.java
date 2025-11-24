package data.user;

import usecase.auth.PasswordHasher;

public class PasswordHash implements PasswordHasher {

    @Override
    public String hash(String rawPassword) {
        return Integer.toHexString(rawPassword.hashCode());
    }
}
