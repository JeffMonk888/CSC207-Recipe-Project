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
 *
 * This class wires the Clean Architecture stack:
 *  View -> Controller -> Interactor -> Gateway
 *
 * It also contains OPTIONAL demo seeding logic. In the real application,
 * you should already have saved recipes in the CSV, so the seeding method
 * will simply exit without adding any data.
 */
public class RateRecipeDemo {

    private static final long USER_ID = 1L;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ===== Gateway for saved recipes =====
            UserSavedRecipeAccessObject gateway =
                    new UserSavedRecipeAccessObject("user_recipe_links.csv");

            // OPTIONAL: seed demo data only when there are no saved recipes at all.
            seedDemoDataIfEmpty(gateway);

            // ===== ViewModel and ViewManager =====
            RateRecipeViewModel viewModel = new RateRecipeViewModel();
            ViewManagerModel viewManagerModel = new ViewManagerModel();

            // ===== Presenter and Interactor =====
            RateRecipeOutputBoundary presenter =
                    new RateRecipePresenter(viewModel, viewManagerModel);
            RateRecipeInputBoundary interactor =
                    new RateRecipeInteractor(gateway, presenter);

            // ===== Controller =====
            RateRecipeController controller = new RateRecipeController(interactor);

            // ===== View (Clean Architecture-compliant) =====
            RateRecipeView view = new RateRecipeView(controller, viewModel, gateway);
            view.setVisible(true);
        });
    }

    /**
     * Seed two demo recipes for USER_ID if none exist yet.
     *
     * IMPORTANT:
     *  - This is ONLY for the standalone demo.
     *  - In the real application, "real" saved recipes for this user
     *    will come from other use cases that write to the same CSV.
     */
    private static void seedDemoDataIfEmpty(UserSavedRecipeAccessObject gateway) {
        if (!gateway.findByUserId(USER_ID).isEmpty()) {
            // There are already real saved recipes; do not overwrite.
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
