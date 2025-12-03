package interfaceadapter.rate_recipe;

import domain.entity.UserRating;
import interfaceadapter.ViewManagerModel;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import usecase.rate_recipe.RateRecipeOutputData;

/**
 * Presenter for UC9: Favourite / Rate Recipe.
 *
 * Converts the raw output data from the interactor into the RateRecipeState
 * that the Swing view observes via the RateRecipeViewModel.
 */
public class RateRecipePresenter implements RateRecipeOutputBoundary {

    private final RateRecipeAbstractViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public RateRecipePresenter(RateRecipeAbstractViewModel viewModel,
                               ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentSuccess(RateRecipeOutputData outputData) {
        RateRecipeState state = new RateRecipeState(viewModel.getState());

        if (outputData.isRemoved()) {
            // Rating was cleared
            state.setRatingRemoved(true);
            state.setStars(null);
            state.setMessage("Rating cleared.");
        } else {
            // Rating was saved/updated
            UserRating rating = outputData.getRating();
            if (rating != null) {
                state.setRatingRemoved(false);
                state.setRecipeId(rating.getRecipeId());
                state.setStars(rating.getStars());
                state.setMessage("Rating saved.");
            } else {
                // Defensive fallback: treat null rating as "cleared".
                state.setRatingRemoved(true);
                state.setStars(null);
                state.setMessage("Rating cleared.");
            }
        }

        // On success we clear any previous error message.
        state.setErrorMessage(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();

        // Optionally, the presenter could switch views here if your flow
        // requires it, for example back to "saved recipes":
        //
        // viewManagerModel.setActiveViewName("saved recipes");
        //
        // For now, we do not force a view switch.
    }

    @Override
    public void presentFailure(String errorMessage) {
        RateRecipeState state = new RateRecipeState(viewModel.getState());
        state.setErrorMessage(errorMessage);
        state.setMessage(null); // Clear any previous success message
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }
}
