package usecase.category.delete_category;

public interface DeleteCategoryOutputBoundary {
    void presentSuccess(DeleteCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
