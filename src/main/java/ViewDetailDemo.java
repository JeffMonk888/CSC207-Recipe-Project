// Main.java  (no package, or put it in `dev` if you want)
import data.api.SpoonacularClient;
import domain.entity.Recipe;
import usecase.view_recipe.*;
import domain.entity.NutritionInfo;
import domain.entity.InstructionStep;

public class ViewDetailDemo {
    public static void main(String[] args) {
        // 1. API key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            // TEMP ONLY for local testing; don't commit the key
            apiKey = "7379cb18b81945e4994504e9414ff7f1";
        }

        SpoonacularClient client = new SpoonacularClient(apiKey);

        // presenter
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

                System.out.println("=== Steps ===");
                if (r.getInstructionSteps().isEmpty()) {
                    System.out.println("  (no steps provided)");
                } else {
                    for (InstructionStep step : r.getInstructionSteps()) {
                        System.out.println(" " + step.getStepNumber() + ". " + step.getDescription());
                    }
                }

                System.out.println("=== Nutrition Info ===");
                NutritionInfo n = r.getNutritionInfo();
                if (n == null) {
                    System.out.println("  (no nutrition data available)");
                } else {
                    System.out.println("Calories:       " + n.getCalories());
                    System.out.println("Protein:        " + n.getProtein());
                    System.out.println("Fat:            " + n.getFat());
                    System.out.println("Carbohydrates:  " + n.getCarbohydrates());
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("FAILED: " + errorMessage);
            }
        };

        // 3. Interactor
        ViewRecipeInputBoundary interactor = new ViewRecipeInteractor(client, presenter);

        // 4. Pick a Spoonacular recipe id to test
        long testId = 1003464;
        interactor.execute(new ViewRecipeInputData(testId));
    }
}
