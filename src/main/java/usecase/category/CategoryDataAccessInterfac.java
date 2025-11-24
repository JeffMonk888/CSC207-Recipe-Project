package usecase.category;

import domain.entity.Category;

import java.util.List;

/**
 * Data access interface for UC10: Create/Customise Category for filter.
 *
 * Implementations live in the data layer.
 */
public interface CategoryDataAccessInterface {

    boolean categoryNameExists(Long userId, String name);

    Category createCategory(Long userId, String name);

    boolean categoryExistsForUser(Long userId, Long categoryId);

    List<Category> findCategoriesForUser(Long userId);

    void assignRecipesToCategory(Long userId, Long categoryId, List<Long> recipeIds);

    List<Long> getRecipeIdsForCategory(Long userId, Long categoryId);

    void deleteCategory(Long userId, Long categoryId);
}
