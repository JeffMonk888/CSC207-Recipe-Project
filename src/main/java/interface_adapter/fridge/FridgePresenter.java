package interface_adapter.fridge;

import usecase.add_ingredient.AddIngredientOutputBoundary;
import usecase.add_ingredient.AddIngredientOutputData;
import usecase.remove_ingredient.RemoveIngredientOutputBoundary;
import usecase.remove_ingredient.RemoveIngredientOutputData;

import java.util.ArrayList;
import java.util.List;

public class FridgePresenter implements
        AddIngredientOutputBoundary,
        RemoveIngredientOutputBoundary {

    private final FridgeViewModel fridgeViewModel;

    public FridgePresenter(FridgeViewModel fridgeViewModel) {
        this.fridgeViewModel = fridgeViewModel;
    }

    // Add Ingredient
    @Override
    public void presentSuccess(AddIngredientOutputData outputData) {
        FridgeState state = fridgeViewModel.getState();

        List<String> updated = new ArrayList<>(state.getIngredients());
        updated.add(outputData.getIngredient());

        state.setIngredients(updated);
        state.setCurrentIngredient("");
        state.setErrorMessage(null);

        fridgeViewModel.fireStateChanged();
    }

    // Remove Ingredient
    @Override
    public void presentSuccess(RemoveIngredientOutputData outputData) {
        FridgeState state = fridgeViewModel.getState();

        List<String> updated = new ArrayList<>(state.getIngredients());
        updated.remove(outputData.getRemovedIngredient());

        state.setIngredients(updated);
        state.setErrorMessage(null);

        fridgeViewModel.fireStateChanged();
    }

    // failure handling
    @Override
    public void presentFailure(String errorMessage) {
        FridgeState state = fridgeViewModel.getState();
        state.setErrorMessage(errorMessage);
        fridgeViewModel.fireStateChanged();
    }
}
