// Main.java  (no package, or put it in `dev` if you want)
import data.api.SpoonacularClient;
import data.rating.InMemoryUserRatingGateway;
import domain.entity.Recipe;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import domain.entity.UserRating;
import usecase.view_recipe.*;
import usecase.rate_recipe.*;

public class Main {
    public static void main(String[] args) {
        // 1. API key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            // TEMP ONLY for local testing; don't commit the key
            apiKey = "ef09f685ac104edbbac1ce1bc9ff8028";
        }

        SpoonacularClient client = new SpoonacularClient(apiKey);

        // 2. Simple console presenter
        ViewRecipeOutputBoundary presenter = new ViewRecipeOutputBoundary() {
            @Override
            public void presentSuccess(ViewRecipeOutputData outputData) {
                Recipe r = outputData.getRecipe();
                System.out.println("=== RECIPE DETAILS ===");
                System.out.println("ID: " + r.getId());
                System.out.println("Title: " + r.getTitle());
                System.out.println("Servings: " + r.getServings());
                System.out.println("Ready in: " + r.getPrepTimeInMinutes() + " min");
                System.out.println("Image: " + r.getImage());
                System.out.println("\nIngredients:");
                r.getIngredients().forEach(ing ->
                        System.out.println(" - " + ing.getOriginalString())
                );
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("FAILED: " + errorMessage);
            }
        };

        // 3. Interactor
        ViewRecipeInputBoundary interactor = new ViewRecipeInteractor(client, presenter);

        // 4. Pick a Spoonacular recipe id to test
        long testId = 716429L;
        interactor.execute(new ViewRecipeInputData(testId));
    }
}
