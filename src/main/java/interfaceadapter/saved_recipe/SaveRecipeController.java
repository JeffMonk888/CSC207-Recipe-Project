package interfaceadapter.saved_recipe;

import usecase.save_recipe.SaveRecipeInputBoundary;
import usecase.save_recipe.SaveRecipeInputData;

public class SaveRecipeController {
    private final SaveRecipeInputBoundary saveUseCase;

    public SaveRecipeController(SaveRecipeInputBoundary saveUseCase) {
        this.saveUseCase = saveUseCase;
    }

    public void execute(Long userId, String recipeKey) {
        SaveRecipeInputData data = new SaveRecipeInputData(userId, recipeKey);
        saveUseCase.execute(data);
    }
}
