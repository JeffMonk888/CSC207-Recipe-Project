package demo;

import data.saved_ingredient.FileFridgeAccessObject;
import interface_adapter.search_by_fridge.SearchByFridgeController;
import interface_adapter.search_by_fridge.SearchByFridgePresenter;
import interface_adapter.search_by_fridge.SearchByFridgeViewModel;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;
import usecase.search_by_fridge.SearchByFridgeInputBoundary;
import usecase.search_by_fridge.SearchByFridgeInteractor;
import usecase.search_by_fridge.SearchByFridgeOutputBoundary;
import data.api.SpoonacularClient; // <- adjust package if needed
import view.SearchByFridgeView;

import javax.swing.*;

/**
 * Standalone demo for the SearchByFridgeView.
 * This bypasses the full app ViewManager and just opens a single window.
 */
public class SearchByFridgeViewDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FridgeAccess fridgeAccess = new FileFridgeAccessObject("fridge_items.csv");

            RecipeByIngredientsAccess recipeAccess = new SpoonacularClient("7379cb18b81945e4994504e9414ff7f1");

            SearchByFridgeViewModel viewModel = new SearchByFridgeViewModel();
            SearchByFridgeOutputBoundary presenter = new SearchByFridgePresenter(viewModel);

            SearchByFridgeInputBoundary interactor =
                    new SearchByFridgeInteractor(fridgeAccess, recipeAccess, presenter);

            SearchByFridgeController controller =
                    new SearchByFridgeController(interactor);

            Long demoUserId = 1L;

            SearchByFridgeView searchView =
                    new SearchByFridgeView(controller, viewModel, demoUserId);

            JFrame frame = new JFrame("Search By Fridge Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(searchView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
