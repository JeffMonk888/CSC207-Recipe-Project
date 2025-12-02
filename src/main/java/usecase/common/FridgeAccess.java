package usecase.common;

import java.util.List;

/**
 * Interface for accessing an modifying a user's fridge.
 *
 * <p>This interface defines the operations that the application's
 * use case can perform on stored ingredients. Implementations of this interface handle
 * the actual data storage mechanism.</p>
 *
 */
public interface FridgeAccess {

    /**
     * Checks whether the specific user has a particular ingredient.
     *
     * @param userId the ID of the user whose fridge is being checked
     * @param item the ingredient to check for
     * @return {@code true} if the ingredient exists in the user's fridge,
     *         {@code false} otherwise
     */
    boolean hasItem(Long userId, String item);

    /**
     * Adds an ingredient to the specified user's fridge.
     *
     * @param userId the ID of the user whose fridge is being updated
     * @param item the ingredient to add
     */
    void addItem(Long userId, String item);

    /**
     * Removes an ingredient from the specified user's fridge.
     *
     * @param userId the ID of the user whose fridge is being updated
     * @param item the ingredient to delete
     * @return {@code true} if te ingredient existed and was removed,
     *         {@code false} if the ingredient was not found
     */
    boolean removeItem(Long userId, String item);

    /**
     * Retrieves all ingredients currently stored in the user's fridge.
     *
     * @param userId the ID of the user whose fridge contents are requested
     * @return a list of ingredient names, may be empty but never {@code null}
     */
    List<String> getItems(Long userId);
}
