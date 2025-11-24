package usecase.category;

import java.util.List;

public class AssignCategoryInputData {

    private final Long userId;
    private final Long categoryId;
    private final List<Long> recipeIds;

    public AssignCategoryInputData(Long userId, Long categoryId, List<Long> recipeIds) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.recipeIds = recipeIds;
    }

    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
    public List<Long> getRecipeIds() { return recipeIds; }
}
