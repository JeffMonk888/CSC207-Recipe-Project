import data.rating.InMemoryUserRatingGateway;
import domain.entity.UserRating;
import usecase.rate_recipe.*;

public class RateRecipeDemo {

    public static void main(String[] args) {
        long userId = 1L;
        long recipeId = 42L;

        // 1. gateway & presenter
        InMemoryUserRatingGateway gateway = new InMemoryUserRatingGateway();

        RateRecipeOutputBoundary presenter = new RateRecipeOutputBoundary() {
            @Override
            public void presentSuccess(RateRecipeOutputData outputData) {
                if (outputData.isRemoved()) {
                    System.out.println("Rating removed successfully.");
                } else {
                    UserRating rating = outputData.getRating();
                    System.out.println("Rating saved:");
                    System.out.println("  userId   = " + rating.getUserId());
                    System.out.println("  recipeId = " + rating.getRecipeId());
                    System.out.println("  stars    = " + rating.getStars());
                    System.out.println("  updated  = " + rating.getUpdatedAt());
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("FAILED: " + errorMessage);
            }
        };

        RateRecipeInputBoundary interactor =
                new RateRecipeInteractor(gateway, presenter);

        // 2. rate 4.5 stars
        System.out.println("=== Give 4.5 stars ===");
        interactor.execute(new RateRecipeInputData(userId, recipeId, 4.5));

        // 3. update to 3.0 stars
        System.out.println("\n=== Update to 3.0 stars ===");
        interactor.execute(new RateRecipeInputData(userId, recipeId, 3.0));

        // 4. delete rating（0 star）
        System.out.println("\n=== Remove rating (0.0) ===");
        interactor.execute(new RateRecipeInputData(userId, recipeId, 0.0));
    }
}
