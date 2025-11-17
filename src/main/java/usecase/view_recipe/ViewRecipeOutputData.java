package usecase.view_recipe;

import domain.entity.Recipe;

public class ViewRecipeOutputData {
    private final Recipe recipe;
    public ViewRecipeOutputData(Recipe recipe) {
        this.recipe = recipe;
    }
    public Recipe getRecipe() {
        return recipe;
    }
}
