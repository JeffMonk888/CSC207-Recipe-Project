package interface_adapter.rate_recipe;

import domain.entity.UserRating;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import usecase.rate_recipe.RateRecipeOutputData;

/**
 * Presenter for UC9: Favourite / Rate Recipe.
 *
 * It turns the output data from the interactor into a {@link RateRecipeState}
 * so that the GUI can show clear feedback to the user.
 */
public class RateRecipePresenter implements RateRecipeOutputBoundary {

    private final RateRecipeViewModel viewModel;

    public RateRecipePresenter(RateRecipeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(RateRecipeOutputData outputData) {
        RateRecipeState state = viewModel.getState();
        state.setRemoved(outputData.isRemoved());

        if (outputData.isRemoved()) {
            // Rating has been cleared.
            state.setStars(null);
            state.setMessage("Rating cleared.");
        } else {
            // Rating has been created or updated.
            UserRating rating = outputData.getRating();
            if (rating != null) {
                state.setRecipeId(rating.getRecipeId());
                state.setStars(rating.getStars());
                state.setMessage("Rating saved: " + rating.getStars());
            } else {
                // Defensive branch: should not normally happen.
                state.setStars(null);
                state.setMessage("Rating updated.");
            }
        }

        viewModel.fireStateChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        RateRecipeState state = viewModel.getState();
        state.setMessage(errorMessage);
        state.setRemoved(false);

        viewModel.fireStateChanged();
    }
}
