package usecase.category.assign_category;

public interface AssignCategoryOutputBoundary {
    void presentSuccess(AssignCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
