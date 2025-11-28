package demo;// Main.java  (no package, or put it in `dev` if you want)
import data.api.SpoonacularClient;
import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;
import usecase.view_recipe.*;
import domain.entity.NutritionInfo;
import domain.entity.InstructionStep;

public class ViewRecipeDataDemo {
    public static void main(String[] args) {
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "6586492a77f54829ba878d12fb62832d";
        }

        SpoonacularClient client = new SpoonacularClient(apiKey);

        RecipeDataAssessObject recipeCache =
                new RecipeDataAssessObject("recipe_cache.json");

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

        ViewRecipeInputBoundary interactor = new ViewRecipeInteractor(client, recipeCache, presenter);

        String testId = "a1003464";
        interactor.execute(new ViewRecipeInputData(testId));
    }
}
