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
        Long userId = inputData.getUserId();
        String ingredient = inputData.getIngredient();

        if (userId == null) {
            presenter.presentFailure("User ID cannot be null.");
            return;
        }

        if (ingredient == null || ingredient.trim().isEmpty()) {
            presenter.presentFailure("Ingredient cannot be empty.");
            return;
        }

        String trimmed = ingredient.trim();

        boolean removed = fridgeAccess.removeItem(userId, trimmed);

        if (removed) {
            RemoveIngredientOutputData outputData =
                    new RemoveIngredientOutputData(trimmed);
            presenter.presentSuccess(outputData);
        } else {
            presenter.presentFailure("Ingredient not found in your fridge.");
        }
    }
}
