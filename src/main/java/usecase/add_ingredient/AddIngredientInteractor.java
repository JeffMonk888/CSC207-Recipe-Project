package usecase.add_ingredient;

import usecase.common.FridgeAccess;

import java.util.List;

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

        // Optional rule: no duplicates for the same user
        List<String> existing = fridgeAccess.getItems(userId);
        if (existing.contains(trimmed)) {
            presenter.presentFailure("Ingredient is already in your fridge.");
            return;
        }

        // Business rule satisfied → update storage
        fridgeAccess.addItem(userId, trimmed);

        AddIngredientOutputData outputData =
                new AddIngredientOutputData(userId, trimmed);
        presenter.presentSuccess(outputData);
    }
}
