package domain.entity;

import java.time.Instant;

/**
 * Domain entity: rating that a user gives to a recipe.
 * UC9 Favourite / Rate Recipe.
 */
public class UserRating {

    private Long id;
    private Long userId;
    private Long recipeId;
    private int stars;          // integer 1..5
    private Instant updatedAt;

    public UserRating(Long id, Long userId, Long recipeId,
                      int stars, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.recipeId = recipeId;
        this.stars = stars;
        this.updatedAt = updatedAt;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserRating{" +
                "userId=" + userId +
                ", recipeId=" + recipeId +
                ", stars=" + stars +
                ", updatedAt=" + updatedAt +
                '}';
    }
}