package usecase.category;

public interface DeleteCategoryOutputBoundary {
    void presentSuccess(DeleteCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
