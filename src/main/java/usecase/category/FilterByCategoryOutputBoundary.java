package usecase.category;

public interface FilterByCategoryOutputBoundary {
    void presentSuccess(FilterByCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
