package usecase.category.remove_recipe;

/**
 * Output data for the use case "remove a recipe from a category".
 */
public class RemoveRecipeFromCategoryOutputData {

    private final Long categoryId;
    private final Long recipeId;

    public RemoveRecipeFromCategoryOutputData(Long categoryId, Long recipeId) {
        this.categoryId = categoryId;
        this.recipeId = recipeId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getRecipeId() {
        return recipeId;
    }
}
