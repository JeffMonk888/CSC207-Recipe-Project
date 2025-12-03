package usecase.delete_saved;

import usecase.common.MotionForRecipe;

public class DeleteSavedInputData {
    private final Long userId;
    private final String recipeKey;

    public DeleteSavedInputData(Long userId, String recipeKey) {
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


