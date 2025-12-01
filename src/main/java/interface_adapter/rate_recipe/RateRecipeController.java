package interface_adapter.rate_recipe;

import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInputData;

/**
 * Controller for UC9: Favourite / Rate Recipe.
 *
 * It translates UI events into calls to the use case interactor.
 */
public class RateRecipeController {

    private final RateRecipeInputBoundary interactor;

    public RateRecipeController(RateRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Set or update the rating for a recipe.
     *
     * @param userId   the current user id
     * @param recipeId the recipe identifier
     * @param stars    rating between 0.0 and 5.0 with step 0.5
     */
    public void rate(long userId, String recipeId, double stars) {
        RateRecipeInputData inputData =
                RateRecipeInputData.forRating(userId, recipeId, stars);
        interactor.execute(inputData);
    }

    /**
     * Clear the rating for a recipe for a given user.
     *
     * @param userId   the current user id
     * @param recipeId the recipe identifier
     */
    public void clear(long userId, String recipeId) {
        RateRecipeInputData inputData =
                RateRecipeInputData.forClear(userId, recipeId);
        interactor.execute(inputData);
    }
}
