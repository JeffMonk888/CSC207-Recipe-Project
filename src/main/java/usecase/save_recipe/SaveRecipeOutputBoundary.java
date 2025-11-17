package usecase.save_recipe;

public interface SaveRecipeOutputBoundary {
    void presentSuccess(SaveRecipeOutputData outputData);
    void presentFailure(String errorMessage);
}
