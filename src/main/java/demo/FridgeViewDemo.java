package demo;

import data.saved_ingredient.FileFridgeAccessObject;
import interface_adapter.fridge.FridgeController;
import interface_adapter.fridge.FridgePresenter;
import interface_adapter.fridge.FridgeState;
import interface_adapter.fridge.FridgeViewModel;
import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInteractor;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInteractor;
import usecase.common.FridgeAccess;
import view.FridgeView;
import javax.swing.*;

/**
 * Standalone demo for the FridgeView.
 * This bypasses the full app ViewManager and just opens a single window.
 */
public class FridgeViewDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            String csvPath = "fridge_items.csv"; // adjust path if needed
            FridgeAccess fridgeAccess = new FileFridgeAccessObject(csvPath);

            FridgeViewModel fridgeViewModel = new FridgeViewModel();
            FridgePresenter fridgePresenter = new FridgePresenter(fridgeViewModel);

            AddIngredientInputBoundary addInteractor =
                    new AddIngredientInteractor(fridgeAccess, fridgePresenter);
            RemoveIngredientInputBoundary removeInteractor =
                    new RemoveIngredientInteractor(fridgeAccess, fridgePresenter);

            FridgeController fridgeController =
                    new FridgeController(addInteractor, removeInteractor);

            Long demoUserId = 1L;

            //FridgeView fridgeView = new FridgeView(fridgeController, fridgeViewModel, demoUserId);

            FridgeState initialState = fridgeViewModel.getState();
            initialState.setIngredients(fridgeAccess.getItems(demoUserId));
            initialState.setErrorMessage(null);
            fridgeViewModel.fireStateChanged();

            JFrame frame = new JFrame("Fridge View Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //frame.setContentPane(fridgeView);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
