package interface_adapter.category;

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

import java.util.List;

/**
 * Controller for UC10: Category management.
 *
 * This class is called by the UI layer (Swing views). It translates
 * primitive UI inputs into input data objects for the use cases,
 * and then delegates to the corresponding interactors.
 */
public class CategoryController {

    private final CreateCategoryInputBoundary createCategoryInteractor;
    private final DeleteCategoryInputBoundary deleteCategoryInteractor;
    private final AssignCategoryInputBoundary assignCategoryInteractor;
    private final FilterByCategoryInputBoundary filterByCategoryInteractor;
    private final RemoveRecipeFromCategoryInputBoundary removeRecipeFromCategoryInteractor;

    public CategoryController(CreateCategoryInputBoundary createCategoryInteractor,
                              DeleteCategoryInputBoundary deleteCategoryInteractor,
                              AssignCategoryInputBoundary assignCategoryInteractor,
                              FilterByCategoryInputBoundary filterByCategoryInteractor,
                              RemoveRecipeFromCategoryInputBoundary removeRecipeFromCategoryInteractor) {
        this.createCategoryInteractor = createCategoryInteractor;
        this.deleteCategoryInteractor = deleteCategoryInteractor;
        this.assignCategoryInteractor = assignCategoryInteractor;
        this.filterByCategoryInteractor = filterByCategoryInteractor;
        this.removeRecipeFromCategoryInteractor = removeRecipeFromCategoryInteractor;
    }

    /**
     * Create a new category for the given user.
     */
    public void createCategory(Long userId, String name) {
        CreateCategoryInputData inputData =
                new CreateCategoryInputData(userId, name);
        createCategoryInteractor.execute(inputData);
    }

    /**
     * Delete an existing category for the given user.
     */
    public void deleteCategory(Long userId, Long categoryId) {
        DeleteCategoryInputData inputData =
                new DeleteCategoryInputData(userId, categoryId);
        deleteCategoryInteractor.execute(inputData);
    }

    /**
     * Assign a list of recipes to a category for the given user.
     */
    public void assignRecipesToCategory(Long userId,
                                        Long categoryId,
                                        List<String> recipeIds) {
        AssignCategoryInputData inputData =
                new AssignCategoryInputData(userId, categoryId, recipeIds);
        assignCategoryInteractor.execute(inputData);
    }

    /**
     * Filter saved recipes by category for the given user.
     * The result will be passed to the presenter.
     */
    public void filterByCategory(Long userId, Long categoryId) {
        FilterByCategoryInputData inputData =
                new FilterByCategoryInputData(userId, categoryId);
        filterByCategoryInteractor.execute(inputData);
    }

    /**
     * Remove a single recipe from a category for the given user.
     */
    public void removeRecipeFromCategory(Long userId,
                                         Long categoryId,
                                         String recipeId) {
        RemoveRecipeFromCategoryInputData inputData =
                new RemoveRecipeFromCategoryInputData(userId, categoryId, recipeId);
        removeRecipeFromCategoryInteractor.execute(inputData);
    }
}
