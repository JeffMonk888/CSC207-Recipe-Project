package usecase.rate_recipe;

import domain.entity.UserRating;

import java.time.Instant;

public class RateRecipeInteractor implements RateRecipeInputBoundary {

    private final UserRatingGateway ratingGateway;
    private final RateRecipeOutputBoundary presenter;

    public RateRecipeInteractor(UserRatingGateway ratingGateway,
                                RateRecipeOutputBoundary presenter) {
        this.ratingGateway = ratingGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(RateRecipeInputData inputData) {
        int stars = inputData.getStars();

        // 1. 验证评分：只能是 1,2,3,4,5
        if (!isValidStars(stars)) {
            presenter.presentFailure("Rating must be an integer between 1 and 5.");
            return;
        }

        long userId = inputData.getUserId();
        long recipeId = inputData.getRecipeId();

        // 2. 查找已有评分
        UserRating rating = ratingGateway.findByUserAndRecipe(userId, recipeId);
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

        // 3. 保存
        ratingGateway.save(rating);

        // 4. 通知成功
        presenter.presentSuccess(new RateRecipeOutputData(rating));
    }

    private boolean isValidStars(int stars) {
        return stars >= 1 && stars <= 5;
    }
}