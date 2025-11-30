package usecase.category.remove_recipe;

/**
 * Output data for the use case "remove a recipe from a category".
 */
public class RemoveRecipeFromCategoryOutputData {

    private final Long categoryId;
    private final String recipeId;

    public RemoveRecipeFromCategoryOutputData(Long categoryId, String recipeId) {
        this.categoryId = categoryId;
        this.recipeId = recipeId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getRecipeId() {
        return recipeId;
    }
}
