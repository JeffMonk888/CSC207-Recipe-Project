package interface_adapter.saved_recipe;

import domain.entity.Recipe;
import interface_adapter.ViewManagerModel;
import usecase.delete_saved.DeleteSavedOutputBoundary;
import usecase.delete_saved.DeleteSavedOutputData;
import usecase.retrieve_saved.RetrieveSavedOutputBoundary;
import usecase.retrieve_saved.RetrieveSavedOutputData;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipePresenter implements RetrieveSavedOutputBoundary, DeleteSavedOutputBoundary {

    private final SavedRecipeViewModel viewModel;

    public SavedRecipePresenter(SavedRecipeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(RetrieveSavedOutputData outputData) {
        SavedRecipeState state = viewModel.getState();
        List<String> formattedList = new ArrayList<>();

        for (Recipe recipe : outputData.getSavedRecipes()) {
            formattedList.add(recipe.getTitle() + " [ID:" + recipe.getId() + "]");
        }

        state.setSavedRecipes(formattedList);
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentSuccess(DeleteSavedOutputData outputData) {
        System.out.println("Recipe saved successfully: " + outputData.getDeletedRecipeKey());
    }

    @Override
    public void presentFailure(String errorMessage) {
        SavedRecipeState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
