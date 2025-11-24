package usecase.rate_recipe;

import domain.entity.UserRating;

import java.time.Instant;

/**
 * Interactor for UC9: Favourite / Rate Recipe.
 */
public class RateRecipeInteractor implements RateRecipeInputBoundary {

    private final UserRatingDataAccessInterface ratingDataAccess;
    private final RateRecipeOutputBoundary presenter;

    public RateRecipeInteractor(UserRatingDataAccessInterface ratingDataAccess,
                                RateRecipeOutputBoundary presenter) {
        this.ratingDataAccess = ratingDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(RateRecipeInputData inputData) {
        double stars = inputData.getStars();

        // 1. make sure that ratings are between 0 and 5 in 0.5 increments
        if (!isValidStars(stars)) {
            presenter.presentFailure(
                    "Rating must be between 0 and 5 in 0.5 increments (e.g., 0, 0.5, 1, ..., 5).");
            return;
        }

        long userId = inputData.getUserId();
        long recipeId = inputData.getRecipeId();

        UserRating existing = ratingDataAccess.findByUserAndRecipe(userId, recipeId);

        // 2. rating is 0, delete current rating
        if (stars == 0.0) {
            if (existing == null) {
                presenter.presentFailure("No existing rating to remove for this recipe.");
                return;
            }
            ratingDataAccess.deleteRating(userId, recipeId);
            presenter.presentSuccess(new RateRecipeOutputData(null, true));
            return;
        }

        // 3. create or update ratings
        UserRating rating;
        if (existing == null) {
            rating = new UserRating(
                    null,
                    userId,
                    recipeId,
                    stars,
                    Instant.now()
            );
        } else {
            existing.setStars(stars);
            existing.setUpdatedAt(Instant.now());
            rating = existing;
        }

        // 4. make it last
        ratingDataAccess.save(rating);

        // 5. output to presenter
        presenter.presentSuccess(new RateRecipeOutputData(rating, false));
    }

    private boolean isValidStars(double stars) {
        if (stars < 0.0 || stars > 5.0) {
            return false;
        }
        // 2 * stars should be integer
        double scaled = stars * 2.0;
        return Math.abs(scaled - Math.round(scaled)) < 1e-9;
    }
}
