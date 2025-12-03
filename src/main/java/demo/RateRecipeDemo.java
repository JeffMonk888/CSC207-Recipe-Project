package demo;

import data.saved_recipe.UserSavedRecipeAccessObject;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.rate_recipe.RateRecipeController;
import interfaceadapter.rate_recipe.RateRecipePresenter;
import interfaceadapter.rate_recipe.RateRecipeAbstractViewModel;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import view.RateRecipeView;

/**
 * Standalone demo for the Rate Recipe use case.
 * Uses the real RateRecipeView from the view package.
 */
public class RateRecipeDemo {

    public static void main(String[] args) {

        // Saved-recipe gateway (same file as your app)
        UserSavedRecipeAccessObject gateway =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // Dummy ViewManagerModel just to satisfy presenter constructor
        ViewManagerModel viewManagerModel = new ViewManagerModel();

        // ViewModel + Presenter
        RateRecipeAbstractViewModel rateRecipeViewModel = new RateRecipeAbstractViewModel();
        RateRecipeOutputBoundary rateRecipePresenter =
                new RateRecipePresenter(rateRecipeViewModel, viewManagerModel);

        // Use case + Controller
        RateRecipeInputBoundary rateRecipeInteractor =
                new RateRecipeInteractor(gateway, rateRecipePresenter);
        RateRecipeController rateRecipeController =
                new RateRecipeController(rateRecipeInteractor);

        // For demo, we just use user 1L
        long demoUserId = 1L;

        // Open the real RateRecipeView
        RateRecipeView view = new RateRecipeView(
                rateRecipeController,
                rateRecipeViewModel,
                gateway,
                demoUserId
        );
        view.setVisible(true);
    }
}
