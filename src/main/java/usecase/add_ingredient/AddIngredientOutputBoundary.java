package usecase.add_ingredient;

public interface AddIngredientOutputBoundary {
    void presentSuccess(AddIngredientOutputData outputData);
    void presentFailure(String errorMessage);
}
