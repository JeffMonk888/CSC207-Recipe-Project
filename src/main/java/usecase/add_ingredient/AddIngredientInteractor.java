package usecase.add_ingredient;

import java.util.List;

import usecase.common.FridgeAccess;

public class AddIngredientInteractor implements AddIngredientInputBoundary {

    private final FridgeAccess fridgeAccess;
    private final AddIngredientOutputBoundary presenter;

    public AddIngredientInteractor(FridgeAccess fridgeAccess,
                                   AddIngredientOutputBoundary presenter) {
        this.fridgeAccess = fridgeAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddIngredientInputData inputData) {
        final Long userId = inputData.getUserId();
        final String ingredient = inputData.getIngredient();
        String error = null;
        AddIngredientOutputData outputData = null;

        if (userId == null) {
            error = "User ID code cannot be null.";
        }
        else if (ingredient == null || ingredient.trim().isEmpty()) {
            error = "Ingredient cannot be empty.";
        }
        else {
            final String trimmed = ingredient.trim();

            // no duplicates for the same user
            final List<String> existing = fridgeAccess.getItems(userId);
            if (existing.contains(trimmed)) {
                error = "Ingredient is already in your fridge.";
            }
            else {

                // Business rule satisfied → update storage
                fridgeAccess.addItem(userId, trimmed);
                outputData = new AddIngredientOutputData(userId, trimmed);

            }
        }

        if (error != null) {
            presenter.presentFailure(error);
        }
        else {
            presenter.presentSuccess(outputData);
        }
    }
}
