package usecase.view_recipe;

public class ViewRecipeInputData {
    private final long recipeId;

    public ViewRecipeInputData(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getRecipeId() {
        return recipeId;
    }
}
