// Main.java  (no package, or put it in `dev` if you want)
import data.api.SpoonacularClient;
import domain.entity.Recipe;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import usecase.view_recipe.*;

public class ViewDetailDemo {
    public static void main(String[] args) {
        // API key
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            // TEMP ONLY for local testing; don't commit the key
            apiKey = "ef09f685ac104edbbac1ce1bc9ff8028";
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
                System.out.println("=== Ingredients === ");
                r.getIngredients().forEach(ing ->
                        System.out.println(" - " + ing.getOriginalString())
                );
                System.out.println("\nSteps:");
                if (r.getInstructionSteps().isEmpty()) {
                    System.out.println("  (no steps provided)");
                } else {
                    for (InstructionStep step : r.getInstructionSteps()) {
                        System.out.println(" " + step.getStepNumber() + ". " + step.getDescription());
                    }
                }
                System.out.println("=== Nutrition Info ===");
                NutritionInfo info = r.getNutritionInfo();
                if (info == null) {
                    System.out.println("  (no nutrition data available)");
                } else {
                    System.out.println("Calories:       " + info.getCalories());
                    System.out.println("Protein:        " + info.getProtein());
                    System.out.println("Fat:            " + info.getFat());
                    System.out.println("Carbohydrates:  " + info.getCarbohydrates());
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
        long testId = 324694;
        interactor.execute(new ViewRecipeInputData(testId));
    }
}
