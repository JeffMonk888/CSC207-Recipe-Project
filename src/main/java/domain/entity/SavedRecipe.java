package domain.entity;
import java.time.Instant;

public class SavedRecipe {

    private Long id;
    private final Long userId;
    private final Long recipeId;
    private Instant savedAt;

    private boolean favourite;

    public SavedRecipe(Long userId, Long recipeId) {
        this.id = null;
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

    // Setters
    public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
    public void setId(Long id) { this.id = id; }
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
