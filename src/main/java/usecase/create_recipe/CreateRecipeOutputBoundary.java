package usecase.create_recipe;

public interface CreateRecipeOutputBoundary {
    void presentSuccess(CreateRecipeOutputData outputData);
    void presentFailure(String errorMessage);
}
