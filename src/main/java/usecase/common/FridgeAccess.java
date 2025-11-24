package usecase.common;

import java.util.List;

public interface FridgeAccess {
    boolean hasItem(Long userId, String item);

    void addItem(Long userId, String item);

    boolean removeItem(Long userId, String item);

    List<String> getItems(Long userId);
}
