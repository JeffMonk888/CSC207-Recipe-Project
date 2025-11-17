import java.time.Instant;

public class SavedRecipe {

    private Long id;
    private Long userId;
    private Long recipeId;
    private Instant savedAt;

    private boolean favourite;

    public SavedRecipe(Long id, Long userId, Long recipeId) {
        this.id = id;
        this.userId = userId;
        this.recipeId = recipeId;
        this.savedAt = Instant.now();
        this.favourite = false; 
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRecipeId() { return recipeId; }
    public Instant getSavedAt() { return savedAt; }
    public boolean isFavourite() { return favourite; }

    // Setters -
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
