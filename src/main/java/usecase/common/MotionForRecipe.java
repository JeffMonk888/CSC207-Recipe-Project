package usecase.common;

import domain.entity.SavedRecipe;
import java.util.ArrayList;

public interface MotionForRecipe {
    boolean exists(Long userId, Long recipeId);

    void save(SavedRecipe newSave);

    ArrayList<SavedRecipe> findByUserId(Long userId);

    boolean delete(Long userId, Long recipeId);
}

