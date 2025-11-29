package usecase.category.assign_category;

import java.util.List;

public class AssignCategoryOutputData {

    private final Long categoryId;
    private final List<Long> assignedRecipeIds;

    public AssignCategoryOutputData(Long categoryId, List<Long> assignedRecipeIds) {
        this.categoryId = categoryId;
        this.assignedRecipeIds = assignedRecipeIds;
    }

    public Long getCategoryId() { return categoryId; }
    public List<Long> getAssignedRecipeIds() { return assignedRecipeIds; }
}
