package interface_adapter.category;

import java.util.List;

import usecase.category.assign_category.AssignCategoryInputBoundary;
import usecase.category.assign_category.AssignCategoryInputData;
import usecase.category.create_category.CreateCategoryInputBoundary;
import usecase.category.create_category.CreateCategoryInputData;
import usecase.category.delete_category.DeleteCategoryInputBoundary;
import usecase.category.delete_category.DeleteCategoryInputData;
import usecase.category.filter_by_category.FilterByCategoryInputBoundary;
import usecase.category.filter_by_category.FilterByCategoryInputData;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInputBoundary;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInputData;

/**
 * Controller for category-related use cases.
 *
 * It translates user actions in the GUI into calls to the corresponding
 * use case interactors.
 */
public class CategoryController {

    private final CreateCategoryInputBoundary createCategoryUseCase;
    private final AssignCategoryInputBoundary assignCategoryUseCase;
    private final FilterByCategoryInputBoundary filterByCategoryUseCase;
    private final RemoveRecipeFromCategoryInputBoundary removeRecipeUseCase;
    private final DeleteCategoryInputBoundary deleteCategoryUseCase;

    /**
     * Constructs a new controller.
     *
     * @param createCategoryUseCase   the use case for creating categories
     * @param assignCategoryUseCase   the use case for assigning recipes
     *                                to categories
     * @param filterByCategoryUseCase the use case for filtering recipes
     *                                by category
     * @param removeRecipeUseCase     the use case for removing a recipe
     *                                from a category
     * @param deleteCategoryUseCase   the use case for deleting a category
     */
    public CategoryController(
            CreateCategoryInputBoundary createCategoryUseCase,
            AssignCategoryInputBoundary assignCategoryUseCase,
            FilterByCategoryInputBoundary filterByCategoryUseCase,
            RemoveRecipeFromCategoryInputBoundary removeRecipeUseCase,
            DeleteCategoryInputBoundary deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.assignCategoryUseCase = assignCategoryUseCase;
        this.filterByCategoryUseCase = filterByCategoryUseCase;
        this.removeRecipeUseCase = removeRecipeUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    /**
     * Create a new category for the given user.
     *
     * @param userId the id of the current user
     * @param name   the name of the new category
     */
    public void createCategory(Long userId, String name) {
        CreateCategoryInputData inputData =
                new CreateCategoryInputData(userId, name);
        createCategoryUseCase.execute(inputData);
    }

    /**
     * Assign one or more saved recipes to an existing category.
     *
     * @param userId     the id of the current user
     * @param categoryId the id of the category
     * @param recipeIds  the ids/keys of the recipes to assign
     */
    public void assignRecipesToCategory(Long userId,
                                        Long categoryId,
                                        List<String> recipeIds) {
        AssignCategoryInputData inputData =
                new AssignCategoryInputData(userId, categoryId, recipeIds);
        assignCategoryUseCase.execute(inputData);
    }

    /**
     * Filter the user's saved recipes by the given category.
     *
     * @param userId     the id of the current user
     * @param categoryId the category to filter by
     */
    public void filterRecipesByCategory(Long userId, Long categoryId) {
        FilterByCategoryInputData inputData =
                new FilterByCategoryInputData(userId, categoryId);
        filterByCategoryUseCase.execute(inputData);
    }

    /**
     * Remove a single recipe from a category.
     *
     * @param userId     the id of the current user
     * @param categoryId the category id
     * @param recipeId   the recipe id/key
     */
    public void removeRecipeFromCategory(Long userId,
                                         Long categoryId,
                                         String recipeId) {
        RemoveRecipeFromCategoryInputData inputData =
                new RemoveRecipeFromCategoryInputData(userId, categoryId, recipeId);
        removeRecipeUseCase.execute(inputData);
    }

    /**
     * Delete an existing category.
     *
     * @param userId     the id of the current user
     * @param categoryId the id of the category to delete
     */
    public void deleteCategory(Long userId, Long categoryId) {
        DeleteCategoryInputData inputData =
                new DeleteCategoryInputData(userId, categoryId);
        deleteCategoryUseCase.execute(inputData);
    }
}
