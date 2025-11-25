package usecase.create_recipe;

public class CreateRecipeOutputData {
    private final String recipeTitle;
    private final String recipeKey;

    public CreateRecipeOutputData(String recipeTitle, String recipeKey) {
        this.recipeTitle = recipeTitle;
        this.recipeKey = recipeKey;
    }

    public String getRecipeTitle() { return recipeTitle; }
    public String getRecipeKey() { return recipeKey; }
}
