package usecase.view_recipe;

public class ViewRecipeInputData {
    private final String recipeKey;

    public ViewRecipeInputData(String recipeId) {
        this.recipeKey = recipeId;
    }

    public String getRecipeKey() {
        return recipeKey;
    }
}
