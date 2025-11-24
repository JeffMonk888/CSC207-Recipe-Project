package usecase.category.filter_by_category;

public class FilterByCategoryInputData {

    private final Long userId;
    private final Long categoryId;

    public FilterByCategoryInputData(Long userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public Long getUserId() { return userId; }
    public Long getCategoryId() { return categoryId; }
}
