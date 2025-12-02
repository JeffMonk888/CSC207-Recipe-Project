package interface_adapter.rate_recipe;

/**
 * State object for UC9: Favourite / Rate Recipe.
 *
 * This is what the RateRecipeViewModel exposes to the Swing view.
 * The view will observe this state (via PropertyChange events) and
 * update its components accordingly.
 */
public class RateRecipeState {

    /**
     * The recipe currently being rated (or whose rating was just changed).
     */
    private String recipeId;

    /**
     * The current rating value (0.0 .. 5.0, step 0.5).
     * Can be null when the rating was cleared.
     */
    private Double stars;

    /**
     * Flag indicating whether the last operation removed the rating.
     */
    private boolean ratingRemoved;

    /**
     * A human-readable message for success (e.g. "Rating saved." or "Rating cleared.").
     */
    private String message;

    /**
     * Error message in case the use case failed (e.g. invalid stars).
     */
    private String errorMessage;

    public RateRecipeState() {
    }

    /**
     * Copy-constructor so presenters can start from an existing state and modify it.
     */
    public RateRecipeState(RateRecipeState copy) {
        this.recipeId = copy.recipeId;
        this.stars = copy.stars;
        this.ratingRemoved = copy.ratingRemoved;
        this.message = copy.message;
        this.errorMessage = copy.errorMessage;
    }

    // Getters and setters

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

    public boolean isRatingRemoved() {
        return ratingRemoved;
    }

    public void setRatingRemoved(boolean ratingRemoved) {
        this.ratingRemoved = ratingRemoved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
