package demo;  // <-- change or remove this to match your project structure

import data.api.SpoonacularClient;
import data.saved_recipe.RecipeDataAssessObject;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.view_recipe.ViewRecipeController;
import interfaceadapter.view_recipe.ViewRecipePresenter;
import interfaceadapter.view_recipe.ViewRecipeAbstractViewModel;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInteractor;
import usecase.view_recipe.ViewRecipeOutputBoundary;
import view.ViewManager;

/**
 * Full end-to-end demo:
 *  - Uses real SpoonacularClient + ViewRecipeInteractor
 *  - Uses ViewRecipePresenter + ViewRecipeViewModel + ViewRecipeView
 *  - Uses ViewManagerModel + ViewManager (CardLayout)
 *  - Triggers the use case for a real Spoonacular recipe ID.
 */
public class ViewRecipeViewDemo {

    public static void main(String[] args) {
        // Get API key (env var preferred)
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "6586492a77f54829ba878d12fb62832d";
        }

        RecipeDataAssessObject recipeCache =
                new RecipeDataAssessObject("recipe_cache.json");

        SpoonacularClient client = new SpoonacularClient(apiKey);

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        ViewManager viewManager = new ViewManager(viewManagerModel);

        ViewRecipeAbstractViewModel viewRecipeViewModel = new ViewRecipeAbstractViewModel();

        ViewRecipeOutputBoundary presenter =
                new ViewRecipePresenter(viewRecipeViewModel, viewManagerModel);

        ViewRecipeInputBoundary interactor =
                new ViewRecipeInteractor(client, recipeCache, presenter);

        ViewRecipeController controller =
                new ViewRecipeController(interactor);

        /**ViewRecipeView viewRecipeView =
                new ViewRecipeView(viewRecipeViewModel, saveRecipeController, viewManagerModel);

        viewManager.addView(viewRecipeView, viewRecipeViewModel.getViewName());*/

        viewManagerModel.setActiveViewName(viewRecipeViewModel.getViewName());

        viewManager.show();

        String testId = "a1003464L";  // recipe ID
        controller.execute(testId);
    }
}
