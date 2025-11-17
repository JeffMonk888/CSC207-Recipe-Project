package usecase.rate_recipe;

import domain.entity.UserRating;

/**
 * Data access interface for UC9 Favourite / Rate Recipe use case.
 * Implementations live in the data layer (e.g. in-memory, file, DB).
 */
public interface UserRatingDataAccessInterface {

    /**
     * Find an existing rating for (userId, recipeId); return null if none exists.
     */
    UserRating findByUserAndRecipe(long userId, long recipeId);

    /**
     * Save (insert or update) the rating.
     */
    void save(UserRating rating);
}