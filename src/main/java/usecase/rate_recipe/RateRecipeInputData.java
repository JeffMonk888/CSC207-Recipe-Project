package usecase.rate_recipe;

public class RateRecipeInputData {

    private final long userId;
    private final long recipeId;
    private final int stars;   // <== int 1..5

    public RateRecipeInputData(long userId, long recipeId, int stars) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.stars = stars;
    }

    public long getUserId() { return userId; }
    public long getRecipeId() { return recipeId; }
    public int getStars() { return stars; }
}
