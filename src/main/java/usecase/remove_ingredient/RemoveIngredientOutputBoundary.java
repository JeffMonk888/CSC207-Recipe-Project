package usecase.remove_ingredient;

public interface RemoveIngredientOutputBoundary {

    void presentSuccess(RemoveIngredientOutputData outputData);
    void presentFailure(String errorMessage);
}
