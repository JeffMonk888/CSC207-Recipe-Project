package usecase.save_recipe;

import domain.entity.SavedRecipe;

public class SaveRecipeOutputData {
    private final SavedRecipe savedRecipe;

    public SaveRecipeOutputData(SavedRecipe savedRecipe) {
        this.savedRecipe = savedRecipe;
    }

    public SavedRecipe getSavedRecipe() {
        return savedRecipe;
    }
}
