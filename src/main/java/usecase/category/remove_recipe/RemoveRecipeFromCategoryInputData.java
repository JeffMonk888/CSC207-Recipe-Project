package usecase.category.remove_recipe;

/**
 * Input data for the use case "remove a recipe from a category".
 */
public class RemoveRecipeFromCategoryInputData {

    private final Long userId;
    private final Long categoryId;
    private final Long recipeId;

    public RemoveRecipeFromCategoryInputData(Long userId, Long categoryId, Long recipeId) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.recipeId = recipeId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getRecipeId() {
        return recipeId;
    }
}
