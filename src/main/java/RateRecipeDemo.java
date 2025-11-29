import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.UserRating;
import usecase.rate_recipe.*;

public class RateRecipeDemo {

    public static void main(String[] args) {
        long userId = 1L;
        long recipeId = 42L;

        // 1. gateway & presenter
        UserSavedRecipeAccessObject gateway =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");

        RateRecipeOutputBoundary presenter = new RateRecipeOutputBoundary() {
            @Override
            public void presentSuccess(RateRecipeOutputData outputData) {
                if (outputData.isRemoved()) {
                    System.out.println("Rating cleared.");
                } else {
                    UserRating rating = outputData.getRating();
                    System.out.println("Rating saved: " + rating);
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Rating failed: " + errorMessage);
            }
        };

        RateRecipeInputBoundary interactor =
                new RateRecipeInteractor(gateway, presenter);

        // 2. Give 4.5 stars
        System.out.println("=== Give 4.5 stars ===");
        interactor.execute(RateRecipeInputData.forRating(userId, recipeId, 4.5));

        // 3. Update to 0.0 stars (still a valid rating)
        System.out.println("\n=== Update to 0.0 stars ===");
        interactor.execute(RateRecipeInputData.forRating(userId, recipeId, 0.0));

        // 4. Clear rating (back to default / empty)
        System.out.println("\n=== Clear rating ===");
        interactor.execute(RateRecipeInputData.forClear(userId, recipeId));
    }
}
