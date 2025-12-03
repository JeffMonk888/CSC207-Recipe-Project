package app;

import javax.swing.SwingUtilities;

import data.saved_ingredient.FileFridgeAccessObject;
import usecase.common.FridgeAccess;

public class Main {

    /**
     * Program entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApp);
    }

    private static void startApp() {
        // Use the REAL fridge access class
        final FridgeAccess fridgeAccess =
                new FileFridgeAccessObject("fridge_items.csv");

        final AppBuilder builder = new AppBuilder(fridgeAccess);

        builder
                .addLoginView()
                .addSignUpView()
                .addHomeView()
                .addFindRecipe()
                .addFridgeFeature()
                .addCreateRecipeFeature()
                .addSavedRecipesFeature()
                .addSearchByFridgeFeature()
                .show();
    }
}
