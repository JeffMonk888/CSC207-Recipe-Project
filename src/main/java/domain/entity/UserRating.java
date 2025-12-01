package domain.entity;

import java.time.Instant;

/**
 * Domain entity: rating that a user gives to a recipe.
 * UC9 Favourite / Rate Recipe.
 *
 * stars: 0.0 .. 5.0 in 0.5 increments.
 * A rating can be cleared; in that case the use case will delete it instead
 * of storing a "special" value.
 */
public class UserRating {

    private Long id;
    private Long userId;
    private String recipeId;
    private double stars;          // 0.0 .. 5.0, step 0.5
    private Instant updatedAt;

    public UserRating(Long id, Long userId, String recipeId, double stars, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.recipeId = recipeId;
        this.stars = stars;
        this.updatedAt = updatedAt;
    }

    /**
     * Convenience constructor used by most code paths.
     * Sets updatedAt to "now" and leaves id null (data layer may assign it).
     */
    public UserRating(long userId, String recipeId, double stars) {
        this(null, userId, recipeId, stars, Instant.now());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }

    public double getStars() { return stars; }
    public void setStars(double stars) { this.stars = stars; }

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
