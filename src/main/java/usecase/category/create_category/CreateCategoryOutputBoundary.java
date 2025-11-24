package usecase.category.create_category;

public interface CreateCategoryOutputBoundary {
    void presentSuccess(CreateCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
