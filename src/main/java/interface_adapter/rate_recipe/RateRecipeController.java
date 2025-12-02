package interface_adapter.rate_recipe;

import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInputData;

/**
 * Controller for UC9: Favourite / Rate Recipe.
 *
 * This class is called by the UI layer (Swing views). It translates
 * primitive UI inputs into an input data object for the use case,
 * and then delegates to the interactor.
 */
public class RateRecipeController {

    private final RateRecipeInputBoundary interactor;

    public RateRecipeController(RateRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Set or update a rating for the given (userId, recipeId).
     *
     * @param userId   ID of the current user
     * @param recipeId ID/key of the recipe being rated
     * @param stars    rating value in [0.0, 5.0] with step 0.5
     */
    public void rate(long userId, String recipeId, double stars) {
        RateRecipeInputData inputData = RateRecipeInputData.forRating(userId, recipeId, stars);
        interactor.execute(inputData);
    }

    /**
     * Clear the rating for the given (userId, recipeId).
     *
     * @param userId   ID of the current user
     * @param recipeId ID/key of the recipe whose rating should be cleared
     */
    public void clearRating(long userId, String recipeId) {
        RateRecipeInputData inputData = RateRecipeInputData.forClear(userId, recipeId);
        interactor.execute(inputData);
    }
}
