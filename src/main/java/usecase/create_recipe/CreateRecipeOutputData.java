package usecase.create_recipe;

public class CreateRecipeOutputData {
    private final String recipeTitle;

    public CreateRecipeOutputData(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public String getRecipeTitle() { return recipeTitle; }
}
