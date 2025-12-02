package usecase.remove_ingredient;

import usecase.common.FridgeAccess;

public class RemoveIngredientInteractor implements RemoveIngredientInputBoundary {

    private final FridgeAccess fridgeAccess;
    private final RemoveIngredientOutputBoundary presenter;

    public RemoveIngredientInteractor(FridgeAccess fridgeAccess,
                                      RemoveIngredientOutputBoundary presenter) {
        this.fridgeAccess = fridgeAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(RemoveIngredientInputData inputData) {
        String message = null;
        RemoveIngredientOutputData outputData = null;

        final Long userId = inputData.getUserId();
        final String ingredient = inputData.getIngredient();

        if (userId == null) {
            message = "User ID cannot be null.";
        }
        else if (ingredient == null || ingredient.trim().isEmpty()) {
            message = "Ingredient cannot be empty.";

        }
        else {
            final String trimmed = ingredient.trim();

            final boolean removed = fridgeAccess.removeItem(userId, trimmed);

            if (removed) {
                outputData = new RemoveIngredientOutputData(trimmed);
                presenter.presentSuccess(outputData);
            }
            else {
                message = "Ingredient not found in your fridge.";
            }
        }

        if (message != null) {
            presenter.presentFailure(message);
        }
        else {
            presenter.presentSuccess(outputData);
        }

    }
}
