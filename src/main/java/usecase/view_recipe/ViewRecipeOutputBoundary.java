package usecase.view_recipe;

public interface ViewRecipeOutputBoundary {
    void presentSuccess(ViewRecipeOutputData outputData);
    void presentFailure(String errorMessage);
}
