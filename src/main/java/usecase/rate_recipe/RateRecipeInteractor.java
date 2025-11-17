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
        int stars = inputData.getStars();

        // 1. 验证评分：只能是 1..5 的整数
        if (!isValidStars(stars)) {
            presenter.presentFailure("Rating must be an integer between 1 and 5.");
            return;
        }

        long userId = inputData.getUserId();
        long recipeId = inputData.getRecipeId();

        // 2. 查询是否已有 rating
        UserRating rating = ratingDataAccess.findByUserAndRecipe(userId, recipeId);
        if (rating == null) {
            rating = new UserRating(
                    null,
                    userId,
                    recipeId,
                    stars,
                    Instant.now()
            );
        } else {
            rating.setStars(stars);
            rating.setUpdatedAt(Instant.now());
        }

        // 3. 持久化
        ratingDataAccess.save(rating);

        // 4. 输出给 presenter
        presenter.presentSuccess(new RateRecipeOutputData(rating));
    }

    private boolean isValidStars(int stars) {
        return stars >= 1 && stars <= 5;
    }
}