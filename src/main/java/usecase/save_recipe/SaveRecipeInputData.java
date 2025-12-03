package usecase.save_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;


public class SaveRecipeInputData {
    private final Long userId;
    private final String recipeKey;

    public SaveRecipeInputData(Long userId, String recipeKey) {
        this.userId = userId;
        this.recipeKey = recipeKey;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRecipeKey() {
        return recipeKey;
    }
}



