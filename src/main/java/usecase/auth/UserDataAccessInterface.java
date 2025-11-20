package usecase.auth;

import domain.entity.User;

public interface UserDataAccessInterface {

    boolean existsByUsername(String username);

    User getByUsername(String username);

    void save(User user);

    long getNextId();
}
