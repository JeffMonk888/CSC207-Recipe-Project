package app;

import data.saved_ingredient.FileFridgeAccessObject;
import usecase.common.FridgeAccess;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Use the REAL fridge access class
            FridgeAccess fridgeAccess =
                    new FileFridgeAccessObject("fridge_items.csv");

            AppBuilder builder = new AppBuilder(fridgeAccess);

            Long dummyUserId = 1L;  // later replaced with real logged-in ID

            builder
                    .addLoginView()
                    .addSignUpView()
                    .addHomeView()
                    .addFindRecipe()
                    .addFridgeFeature(dummyUserId)
                    .addCreateRecipeFeature(dummyUserId)
                    .addSavedRecipesFeature(dummyUserId)
                    .addSearchByFridgeFeature(dummyUserId)
                    .addFilterRecipesFeature()
                    .show();
        });
    }
}
