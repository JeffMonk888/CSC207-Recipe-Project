package usecase.rate_recipe;

import domain.entity.UserRating;

/**
 * Output data for UC9, passed from interactor to presenter.
 *
 * If removed == true, rating will be null.
 */
public class RateRecipeOutputData {

    private final UserRating rating;
    private final boolean removed;

    public RateRecipeOutputData(UserRating rating, boolean removed) {
        this.rating = rating;
        this.removed = removed;
    }

    public UserRating getRating() {
        return rating;
    }

    public boolean isRemoved() {
        return removed;
    }
}
