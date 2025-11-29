package usecase.category.filter_by_category;

public interface FilterByCategoryOutputBoundary {
    void presentSuccess(FilterByCategoryOutputData outputData);
    void presentFailure(String errorMessage);
}
