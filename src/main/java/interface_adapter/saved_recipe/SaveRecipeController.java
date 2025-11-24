package interface_adapter.saved_recipe;

import usecase.save_recipe.SaveRecipeInputBoundary;
import usecase.save_recipe.SaveRecipeInputData;

public class SaveRecipeController {
    private final SaveRecipeInputBoundary saveUseCase;

    public SaveRecipeController(SaveRecipeInputBoundary saveUseCase) {
        this.saveUseCase = saveUseCase;
    }

    public void execute(Long userId, Long recipeId) {
        SaveRecipeInputData data = new SaveRecipeInputData(userId, recipeId);
        saveUseCase.execute(data);
    }
}
