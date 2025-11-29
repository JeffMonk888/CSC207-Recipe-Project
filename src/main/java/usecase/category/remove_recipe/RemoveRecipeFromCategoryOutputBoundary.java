package usecase.category.remove_recipe;

public interface RemoveRecipeFromCategoryOutputBoundary {

    void presentSuccess(RemoveRecipeFromCategoryOutputData outputData);

    void presentFailure(String errorMessage);
}
