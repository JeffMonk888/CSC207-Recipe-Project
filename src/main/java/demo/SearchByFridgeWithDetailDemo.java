package demo;

import data.api.SpoonacularClient;
import data.saved_ingredient.FileFridgeAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.search_by_fridge.SearchByFridgeController;
import interface_adapter.search_by_fridge.SearchByFridgePresenter;
import interface_adapter.search_by_fridge.SearchByFridgeViewModel;
import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.view_recipe.ViewRecipePresenter;
import interface_adapter.view_recipe.ViewRecipeViewModel;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;
import usecase.search_by_fridge.SearchByFridgeInputBoundary;
import usecase.search_by_fridge.SearchByFridgeInteractor;
import usecase.search_by_fridge.SearchByFridgeOutputBoundary;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInteractor;
import usecase.view_recipe.ViewRecipeOutputBoundary;
import view.SearchByFridgeView;
import view.ViewRecipeView;

import javax.swing.*;

/**
 * Demo that links SearchByFridgeView with ViewRecipeView:
 * - Double-click a recipe in the search results to open its details.
 */
public class SearchByFridgeWithDetailDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Data access
            FridgeAccess fridgeAccess = new FileFridgeAccessObject("fridge_items.csv");

            // Spoonacular client used for BOTH search and view-detail
            SpoonacularClient spoonacularClient = new SpoonacularClient("6586492a77f54829ba878d12fb62832d");
            RecipeByIngredientsAccess recipeAccess = spoonacularClient;

            ViewManagerModel  viewManagerModel = new ViewManagerModel();

            // 2. SearchByFridge stack
            SearchByFridgeViewModel searchVM = new SearchByFridgeViewModel();
            SearchByFridgeOutputBoundary searchPresenter =
                    new SearchByFridgePresenter(searchVM);
            SearchByFridgeInputBoundary searchInteractor =
                    new SearchByFridgeInteractor(fridgeAccess, recipeAccess, searchPresenter);
            SearchByFridgeController searchController =
                    new SearchByFridgeController(searchInteractor);

            // 3. ViewRecipe stack
            ViewRecipeViewModel viewRecipeVM = new ViewRecipeViewModel();
            ViewRecipeOutputBoundary viewRecipePresenter =
                    new ViewRecipePresenter(viewRecipeVM, viewManagerModel);
            ViewRecipeInputBoundary viewRecipeInteractor =
                    new ViewRecipeInteractor(spoonacularClient, viewRecipePresenter);
            ViewRecipeController viewRecipeController =
                    new ViewRecipeController(viewRecipeInteractor);

            ViewRecipeView viewRecipeView = new ViewRecipeView(viewRecipeVM);

            // 4. Recipe selection listener: open detail window and call ViewRecipe use case
            SearchByFridgeView.RecipeSelectionListener selectionListener = recipeId -> {
                JFrame detailFrame = new JFrame("Recipe Detail: " + recipeId);
                detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                detailFrame.setContentPane(viewRecipeView);
                detailFrame.pack();
                detailFrame.setLocationRelativeTo(null);
                detailFrame.setVisible(true);

                // Trigger the ViewRecipe use case
                viewRecipeController.execute(recipeId);
            };

            // 5. Choose a demo user (must have ingredients in fridge_items.csv)
            Long demoUserId = 1L;

            // 6. Build the search view
            SearchByFridgeView searchView =
                    new SearchByFridgeView(searchController, searchVM, demoUserId, selectionListener);

            // 7. Show the main search window
            JFrame frame = new JFrame("Search By Fridge Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(searchView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
