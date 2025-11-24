package usecase.category;

public interface AssignCategoryOutputBoundary {
    void presentSuccess(AssignCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
