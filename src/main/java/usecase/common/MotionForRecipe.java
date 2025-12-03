package usecase.common;

import java.util.ArrayList;

import domain.entity.SavedRecipe;

/**
 * Data access interface for accessing and modifying saved recipes (user's collection).
 *
 * <p>
 * This interface defines the operations that the use case layer can perform on a user's
 * list of saved recipes. Implementations typically reside in the data layer
 * (e.g., UserSavedRecipeAccessObject).
 * </p>
 */
public interface MotionForRecipe {

    /**
     * Checks if a specific recipe is already saved by a specific user.
     *
     * @param userId    the unique identifier (ID) of the user.
     * @param recipeKey the unique key of the recipe (e.g., "a123" for API recipes or "c456" for custom ones).
     * @return {@code true} if the user has already saved this recipe; {@code false} otherwise.
     */
    boolean exists(Long userId, String recipeKey);

    /**
     * Saves a new recipe record to the user's collection.
     *
     * <p>
     * If the record already exists, the behavior (overwrite or ignore) depends on the implementation.
     * </p>
     *
     * @param newSave the {@link SavedRecipe} object containing the user ID, recipe key, and other metadata.
     */
    void save(SavedRecipe newSave);

    /**
     * Finds and retrieves the list of all recipes saved by a specific user.
     *
     * @param userId the unique identifier (ID) of the user.
     * @return an {@link ArrayList} containing all {@link SavedRecipe} objects belonging to the user.
     *      Returns an empty list if the user has not saved any recipes.
     */
    ArrayList<SavedRecipe> findByUserId(Long userId);

    /**
     * Removes a specific recipe from a user's collection.
     *
     * @param userId    the unique identifier (ID) of the user.
     * @param recipeKey the unique key of the recipe to be removed.
     * @return {@code true} if the deletion was successful (the recipe existed and was removed);
     *          {@code false} if the record was not found or the deletion failed.
     */
    boolean delete(Long userId, String recipeKey);
}

