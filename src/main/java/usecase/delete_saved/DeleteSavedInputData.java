package usecase.delete_saved;

import usecase.common.MotionForRecipe;

// --- Input Data ---
public class DeleteSavedInputData {
    private final Long userId;
    private final Long recipeId;

    public DeleteSavedInputData(Long userId, Long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }
    public Long getUserId() { return userId; }
    public Long getRecipeId() { return recipeId; }
}

// --- Interactor ---

