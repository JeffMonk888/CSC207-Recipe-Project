package usecase.category.delete_category;

public class DeleteCategoryInputData {

    private final Long userId;
    private final Long categoryId;

    public DeleteCategoryInputData(Long userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
}
