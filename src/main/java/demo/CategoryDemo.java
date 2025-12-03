package demo;

import data.saved_recipe.UserSavedRecipeAccessObject;
import usecase.common.MotionForRecipe;
import view.CategoryView;

import javax.swing.*;

/**
 * Demo entry point for UC10 Category view.
 *
 * This class only creates and shows the CategoryView
 * using a demo userId and the real saved-recipe gateway.
 */
public class CategoryDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Demo user id (for local testing)
            long demoUserId = 1L;

            // Real saved-recipe gateway; implements MotionForRecipe
            MotionForRecipe savedGateway =
                    new UserSavedRecipeAccessObject("user_recipe_links.csv");

            // Use the new constructor: (long userId, MotionForRecipe savedGateway)
            CategoryView view = new CategoryView(demoUserId, savedGateway);
            view.setVisible(true);
        });
    }
}
