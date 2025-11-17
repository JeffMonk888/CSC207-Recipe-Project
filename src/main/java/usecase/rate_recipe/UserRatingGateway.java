package usecase.rate_recipe;

import domain.entity.UserRating;

/**
 * Data-access interface for UserRating.
 * Implementations live in the data layer (e.g. file, database, in-memory).
 */
public interface UserRatingGateway {

    /**
     * Find an existing rating for (userId, recipeId); return null if none.
     */
    UserRating findByUserAndRecipe(long userId, long recipeId);

    /**
     * Save (insert or update) the rating.
     */
    void save(UserRating rating);
}