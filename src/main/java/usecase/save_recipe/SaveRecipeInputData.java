package usecase.save_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

// --- Input Data  ---

public class SaveRecipeInputData {
    private final Long userId;
    private final Long recipeId;

    public SaveRecipeInputData(Long userId, Long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }
    public Long getUserId() { return userId; }
    public Long getRecipeId() { return recipeId; }
}



