package usecase.rate_recipe;

import domain.entity.UserRating;

/**
 * Output data for UC9, passed to the presenter.
 */
public class RateRecipeOutputData {

    private final UserRating rating;

    public RateRecipeOutputData(UserRating rating) {
        this.rating = rating;
    }

    public UserRating getRating() {
        return rating;
    }
}