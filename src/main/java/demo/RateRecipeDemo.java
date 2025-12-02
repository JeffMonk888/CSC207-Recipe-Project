package demo;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import interface_adapter.ViewManagerModel;
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipePresenter;
import interface_adapter.rate_recipe.RateRecipeViewModel;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import view.RateRecipeView;

import javax.swing.*;

/**
 * Demo entry point for UC9 Rate Recipe view.
 */
public class RateRecipeDemo {

    private static final long USER_ID = 1L;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserSavedRecipeAccessObject gateway =
                    new UserSavedRecipeAccessObject("user_recipe_links.csv");

            // Seed demo data only when there are no saved recipes
            seedDemoDataIfEmpty(gateway);

            RateRecipeViewModel viewModel = new RateRecipeViewModel();
            ViewManagerModel viewManagerModel = new ViewManagerModel();

            RateRecipeOutputBoundary presenter =
                    new RateRecipePresenter(viewModel, viewManagerModel);
            RateRecipeInputBoundary interactor =
                    new RateRecipeInteractor(gateway, presenter);

            RateRecipeController controller = new RateRecipeController(interactor);

            // Pass USER_ID explicitly to the view
            RateRecipeView view =
                    new RateRecipeView(controller, viewModel, gateway, USER_ID);
            view.setVisible(true);
        });
    }

    private static void seedDemoDataIfEmpty(UserSavedRecipeAccessObject gateway) {
        if (!gateway.findByUserId(USER_ID).isEmpty()) {
            return;
        }

        SavedRecipe r1 = new SavedRecipe(USER_ID, "101");
        r1.setFavourite(false);
        gateway.save(r1);

        SavedRecipe r2 = new SavedRecipe(USER_ID, "102");
        r2.setFavourite(false);
        gateway.save(r2);

        System.out.println("[Seed] Added demo recipes 101 and 102 for user " + USER_ID);
    }
}
