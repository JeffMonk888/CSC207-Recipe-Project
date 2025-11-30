package usecase.rate_recipe;

/**
 * Input data for UC9.
 *
 * Two modes:
 *  - clearRating == true  -> ignore stars and clear the rating (set it back to default / empty)
 *  - clearRating == false -> stars must be in [0.0, 5.0] with step 0.5
 */
public class RateRecipeInputData {

    private final long userId;
    private final String recipeId;
    private final Double stars;   // nullable when clearRating == true
    private final boolean clearRating;

    public RateRecipeInputData(long userId, String recipeId, Double stars, boolean clearRating) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.stars = stars;
        this.clearRating = clearRating;
    }

    /** Convenience factory: set / update rating. */
    public static RateRecipeInputData forRating(long userId, String recipeId, double stars) {
        return new RateRecipeInputData(userId, recipeId, stars, false);
    }

    /** Convenience factory: clear rating (back to default / empty). */
    public static RateRecipeInputData forClear(long userId, String recipeId) {
        return new RateRecipeInputData(userId, recipeId, null, true);
    }

    public long getUserId() { return userId; }
    public String getRecipeId() { return recipeId; }
    public Double getStars() { return stars; }
    public boolean isClearRating() { return clearRating; }
}
