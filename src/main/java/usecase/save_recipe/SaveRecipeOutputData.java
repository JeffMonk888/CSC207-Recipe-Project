package usecase.save_recipe;

import domain.entity.SavedRecipe; /**
 * Data Transfer Object for carrying successful output to the Presenter.
 */
public class SaveRecipeOutputData {
    private final SavedRecipe savedRecipe;

    public SaveRecipeOutputData(SavedRecipe savedRecipe) {
        this.savedRecipe = savedRecipe;
    }
    public SavedRecipe getSavedRecipe() { return savedRecipe; }
}
