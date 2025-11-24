package usecase.rate_recipe;

/**
 * Input data for UC9.
 */
public class RateRecipeInputData {

    private final long userId;
    private final long recipeId;
    private final double stars;   // 0.0 .. 5.0 in 0.5 increments; 0.0 = remove rating

    public RateRecipeInputData(long userId, long recipeId, double stars) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.stars = stars;
    }

    public long getUserId() { return userId; }
    public long getRecipeId() { return recipeId; }
    public double getStars() { return stars; }
}
