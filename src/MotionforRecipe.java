import java.util.ArrayList;

public interface MotionforRecipe {
    boolean exists(Long userId, Long recipeId);

    void save(Long userId, Long recipeId);

    ArrayList<SavedRecipe> findByUserId(Long userId);

    boolean delete(Long userId, Long recipeId);
}
}
