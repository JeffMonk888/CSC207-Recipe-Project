package data.rating;

import domain.entity.UserRating;
import usecase.rate_recipe.UserRatingGateway;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory implementation of UserRatingGateway.
 * Key: userId + ":" + recipeId
 */
public class InMemoryUserRatingGateway implements UserRatingGateway {

    private final Map<String, UserRating> store = new HashMap<>();

    private String key(long userId, long recipeId) {
        return userId + ":" + recipeId;
    }

    @Override
    public UserRating findByUserAndRecipe(long userId, long recipeId) {
        return store.get(key(userId, recipeId));
    }

    @Override
    public void save(UserRating rating) {
        store.put(key(rating.getUserId(), rating.getRecipeId()), rating);
    }
}
