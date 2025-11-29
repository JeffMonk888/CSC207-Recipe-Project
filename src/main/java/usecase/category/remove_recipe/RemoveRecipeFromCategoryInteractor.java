package usecase.category.remove_recipe;

import usecase.category.CategoryDataAccessInterface;

import java.util.List;

/**
 * Interactor for removing a single recipe from a category (UC10 extension).
 */
public class RemoveRecipeFromCategoryInteractor implements RemoveRecipeFromCategoryInputBoundary {

    private final CategoryDataAccessInterface gateway;
    private final RemoveRecipeFromCategoryOutputBoundary presenter;

    public RemoveRecipeFromCategoryInteractor(CategoryDataAccessInterface gateway,
                                              RemoveRecipeFromCategoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(RemoveRecipeFromCategoryInputData inputData) {
        Long userId = inputData.getUserId();
        Long categoryId = inputData.getCategoryId();
        Long recipeId = inputData.getRecipeId();

        if (userId == null || categoryId == null || recipeId == null) {
            presenter.presentFailure("Missing user, category or recipe id.");
            return;
        }

        if (!gateway.categoryExistsForUser(userId, categoryId)) {
            presenter.presentFailure("Category not found for this user.");
            return;
        }

        List<Long> assigned = gateway.getRecipeIdsForCategory(userId, categoryId);
        if (assigned == null || !assigned.contains(recipeId)) {
            presenter.presentFailure("Recipe is not currently in this category.");
            return;
        }

        gateway.removeRecipeFromCategory(userId, categoryId, recipeId);
        presenter.presentSuccess(new RemoveRecipeFromCategoryOutputData(categoryId, recipeId));
    }
}
