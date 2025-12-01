package usecase.category.assign_category;

import usecase.category.CategoryDataAccessInterface;

import java.util.List;

public class AssignCategoryInteractor implements AssignCategoryInputBoundary {

    private final CategoryDataAccessInterface gateway;
    private final AssignCategoryOutputBoundary presenter;

    public AssignCategoryInteractor(CategoryDataAccessInterface gateway,
                                    AssignCategoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(AssignCategoryInputData inputData) {
        Long userId = inputData.getUserId();
        Long categoryId = inputData.getCategoryId();
        List<String> recipeIds = inputData.getRecipeIds();

        if (!gateway.categoryExistsForUser(userId, categoryId)) {
            presenter.presentFailure("Category not found for this user.");
            return;
        }

        if (recipeIds == null || recipeIds.isEmpty()) {
            presenter.presentFailure("No recipes selected for assignment.");
            return;
        }

        gateway.assignRecipesToCategory(userId, categoryId, recipeIds);
        presenter.presentSuccess(new AssignCategoryOutputData(categoryId, recipeIds));
    }
}
