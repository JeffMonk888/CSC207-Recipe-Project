package domain.entity;
import java.time.Instant;

public class SavedRecipe {

    private Long id;
    private final Long userId;
    private String recipeKey; //  "a123" or "c1"
    private Instant savedAt;

    private boolean favourite;

    public SavedRecipe(Long userId, String recipeKey) {
        this.id = null;
        this.userId = userId;
        this.recipeKey = recipeKey;
        this.savedAt = Instant.now();
        this.favourite = false; 
    }

    public boolean isApiRecipe() { return recipeKey.startsWith("a");}

    public boolean isCustomRecipe() { return recipeKey.startsWith("c");}

    public long getNumericRecipeId() { return Long.parseLong(recipeKey.substring(1));}

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getRecipeKey() { return recipeKey; }
    public Instant getSavedAt() { return savedAt; }
    public boolean isFavourite() { return favourite; }

    // Setters
    public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
    public void setId(Long id) { this.id = id; }
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
