package usecase.category;

public class DeleteCategoryOutputData {

    private final Long deletedCategoryId;

    public DeleteCategoryOutputData(Long deletedCategoryId) {
        this.deletedCategoryId = deletedCategoryId;
    }

    public Long getDeletedCategoryId() {
        return deletedCategoryId;
    }
}
