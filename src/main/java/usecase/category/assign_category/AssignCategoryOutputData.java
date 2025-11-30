package usecase.category.assign_category;

import java.util.List;

public class AssignCategoryOutputData {

    private final Long categoryId;
    private final List<String> assignedRecipeIds;

    public AssignCategoryOutputData(Long categoryId, List<String> assignedRecipeIds) {
        this.categoryId = categoryId;
        this.assignedRecipeIds = assignedRecipeIds;
    }

    public Long getCategoryId() { return categoryId; }
    public List<String> getAssignedRecipeIds() { return assignedRecipeIds; }
}
