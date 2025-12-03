package interfaceadapter.saved_recipe;

import domain.entity.Recipe;
import interfaceadapter.ViewManagerModel;
import usecase.delete_saved.DeleteSavedOutputBoundary;
import usecase.delete_saved.DeleteSavedOutputData;
import usecase.retrieve_saved.RetrieveSavedOutputBoundary;
import usecase.retrieve_saved.RetrieveSavedOutputData;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipePresenter implements RetrieveSavedOutputBoundary, DeleteSavedOutputBoundary {

    private final SavedRecipeAbstractViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public SavedRecipePresenter(SavedRecipeAbstractViewModel viewModel,
                                ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentSuccess(RetrieveSavedOutputData outputData) {
        List<Recipe> recipes = outputData.getSavedRecipes();
        List<String> rows = new ArrayList<>();

        for (Recipe r : recipes) {
            String key = r.getRecipeKey();          // full key: "a632137", "c1764558054954"
            String title = r.getTitle();

            // What will appear in the list:
            // e.g. "c1764558054954 - Banana"  or  "a632137 - Almond Green Bean Salad"
            String line = (key == null ? "" : key) + " - "
                    + (title == null ? "" : title);

            rows.add(line);
        }

        SavedRecipeState state = new SavedRecipeState();
        state.setSavedRecipes(rows);
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();

        // Switch to saved recipes view
        viewManagerModel.setActiveViewName(SavedRecipeAbstractViewModel.VIEW_NAME);
    }

    @Override
    public void presentFailure(String errorMessage) {
        SavedRecipeState state = new SavedRecipeState(viewModel.getState());
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void presentSuccess(DeleteSavedOutputData outputData) {
        // For now, just print to console. The controller can call executeRetrieve(...)
        // afterwards to refresh the list.
        System.out.println("Deleted recipe with key: " + outputData.getDeletedRecipeKey());
    }
}
