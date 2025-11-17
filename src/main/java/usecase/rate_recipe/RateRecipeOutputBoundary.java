package usecase.rate_recipe;

/**
 * Output boundary for UC9: Favourite / Rate Recipe.
 */
public interface RateRecipeOutputBoundary {

    void presentSuccess(RateRecipeOutputData outputData);

    void presentFailure(String errorMessage);
}