package interface_adapter.create_recipe;

import interface_adapter.ViewManagerModel;
import usecase.create_recipe.CreateRecipeOutputBoundary;
import usecase.create_recipe.CreateRecipeOutputData;

public class CreateRecipePresenter implements CreateRecipeOutputBoundary {
    private final CreateRecipeViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public CreateRecipePresenter(CreateRecipeViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentSuccess(CreateRecipeOutputData outputData) {
        CreateRecipeState state = viewModel.getState();
        state.setMessage("Success! Recipe created.\nTitle: " + outputData.getRecipeTitle() + "\nID: " + outputData.getRecipeKey());

        // clear all input
        state.setTitle("");
        state.setIngredients("");
        state.setInstructions("");

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        CreateRecipeState state = viewModel.getState();
        state.setMessage("Error: " + errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
