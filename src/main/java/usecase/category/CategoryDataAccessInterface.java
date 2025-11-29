package usecase.category;

import domain.entity.Category;
import java.util.List;

/**
 * Data access interface for UC10: Create / Customise Category for filter.
 *
 * Implementations live in the data layer.
 */
public interface CategoryDataAccessInterface {

    /**
     * Returns true if the given user already has a category with the same (case-insensitive) name.
     */
    boolean categoryNameExists(Long userId, String name);

    /**
     * Creates a new category for the given user.
     */
    Category createCategory(Long userId, String name);

    /**
     * Returns true if the category with the given id exists and belongs to the given user.
     */
    boolean categoryExistsForUser(Long userId, Long categoryId);

    /**
     * Returns all categories owned by the given user.
     */
    List<Category> findCategoriesForUser(Long userId);

    /**
     * Adds the given recipe ids to the category for this user.
     * The ids are the numeric form of the recipeKey (for example "201" -> 201L).
     */
    void assignRecipesToCategory(Long userId, Long categoryId, List<Long> recipeIds);

    /**
     * Returns the list of recipe ids (numeric) assigned to this category for this user.
     */
    List<Long> getRecipeIdsForCategory(Long userId, Long categoryId);

    /**
     * Removes a single recipe from the category for this user.
     * If the category does not exist for the user, this method does nothing.
     */
    void removeRecipeFromCategory(Long userId, Long categoryId, Long recipeId);

    /**
     * Deletes the category (and all its assignments) for this user.
     * If the category does not exist for the user, this method does nothing.
     */
    void deleteCategory(Long userId, Long categoryId);
}
