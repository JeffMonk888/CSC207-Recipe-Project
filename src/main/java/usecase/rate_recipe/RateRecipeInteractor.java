package usecase.rate_recipe;

import domain.entity.UserRating;

import java.time.Instant;

/**
 * Interactor for UC9: Favourite / Rate Recipe.
 *
 * Requirements (updated version):
 *  - Default is "no rating" (null / empty in storage).
 *  - Rating can be any value in [0.0, 5.0] with step 0.5, including 0.0.
 *  - Clearing rating is an explicit action (input.clearRating == true),
 *    not "rating 0.0".
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
        long userId = inputData.getUserId();
        long recipeId = inputData.getRecipeId();

        // 1. Clear rating mode
        if (inputData.isClearRating()) {
            ratingDataAccess.deleteRating(userId, recipeId);
            presenter.presentSuccess(new RateRecipeOutputData(null, true));
            return;
        }

        // 2. Normal "set/update rating" mode
        Double starsObj = inputData.getStars();
        if (starsObj == null) {
            presenter.presentFailure("Stars cannot be null unless clearRating is true.");
            return;
        }
        double stars = starsObj;

        if (!isValidStars(stars)) {
            presenter.presentFailure(
                    "Rating must be between 0.0 and 5.0 in steps of 0.5."
            );
            return;
        }

        // 3. Find existing rating (if any)
        UserRating rating = ratingDataAccess.findByUserAndRecipe(userId, recipeId);

        if (rating == null) {
            // First time rating
            rating = new UserRating(userId, recipeId, stars);
        } else {
            // Update existing rating
            rating.setStars(stars);
            rating.setUpdatedAt(Instant.now());
        }

        // 4. Persist and notify presenter
        ratingDataAccess.save(rating);
        presenter.presentSuccess(new RateRecipeOutputData(rating, false));
    }

    private boolean isValidStars(double stars) {
        if (stars < 0.0 || stars > 5.0) {
            return false;
        }
        // step 0.5
        double scaled = stars * 2.0;
        return Math.abs(scaled - Math.round(scaled)) < 1e-9;
    }
}
