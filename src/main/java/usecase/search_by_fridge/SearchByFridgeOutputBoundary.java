package usecase.search_by_fridge;

public interface SearchByFridgeOutputBoundary {

    void presentSuccess(SearchByFridgeOutputData outputData);
    void presentFailure(String errorMessage);
}
