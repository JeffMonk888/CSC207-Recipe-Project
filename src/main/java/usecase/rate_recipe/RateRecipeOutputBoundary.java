package usecase.rate_recipe;

public interface RateRecipeOutputBoundary {
    void presentSuccess(RateRecipeOutputData outputData);
    void presentFailure(String errorMessage);
}
