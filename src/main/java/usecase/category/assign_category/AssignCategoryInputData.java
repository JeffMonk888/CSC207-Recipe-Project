package usecase.category.assign_category;

import java.util.List;

public class AssignCategoryInputData {

    private final Long userId;
    private final Long categoryId;
    private final List<String> recipeIds;

    public AssignCategoryInputData(Long userId, Long categoryId, List<String> recipeIds) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.recipeIds = recipeIds;
    }

    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public List<String> getRecipeIds() { return recipeIds; }
}
