package interface_adapter.rate_recipe;

/**
 * View state for UC9: Favourite / Rate Recipe.
 *
 * It holds the last operation result so that a Swing view can display
 * the current rating for a recipe, whether it was removed, and any
 * message for the user.
 */
public class RateRecipeState {

    private String recipeId;
    private Double stars;
    private boolean removed;
    private String message;

    public RateRecipeState() {
    }

    /**
     * Copy constructor.
     *
     * @param copy state to copy from
     */
    public RateRecipeState(RateRecipeState copy) {
        this.recipeId = copy.recipeId;
        this.stars = copy.stars;
        this.removed = copy.removed;
        this.message = copy.message;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
