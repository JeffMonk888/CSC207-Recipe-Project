package data.rating;

import domain.entity.UserRating;
import usecase.rate_recipe.UserRatingDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory implementation of UserRatingDataAccessInterface.
 * Key format: "userId:recipeId".
 */
public class InMemoryUserRatingGateway implements UserRatingDataAccessInterface {

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

    @Override
    public void deleteRating(long userId, long recipeId) {
        store.remove(key(userId, recipeId));
    }
}
