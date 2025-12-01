package demo;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;
import view.CategoryView;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Demo entry point for UC10 Category view.
 *
 * This class seeds some example saved recipes for the demo user and then
 * opens the CategoryView. All demo data generation is kept here instead of
 * inside the view class.
 */
public final class CategoryDemo {

    private static final long USER_ID = 1L;

    private CategoryDemo() {
        // utility class
    }

    public static void main(String[] args) {
        // Seed example saved recipes for the demo user.
        MotionForRecipe gateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");
        seedSavedRecipes(gateway);

        SwingUtilities.invokeLater(() -> {
            CategoryView view = new CategoryView();
            view.setVisible(true);
        });
    }

    /**
     * Seeds some demo saved recipes for the given user if none exist yet.
     *
     * @param gateway data access object used to store saved recipes
     */
    private static void seedSavedRecipes(MotionForRecipe gateway) {
        Set<String> existing = new HashSet<>();
        for (SavedRecipe sr : gateway.findByUserId(USER_ID)) {
            existing.add(sr.getRecipeKey());
        }
        String[] demo = {"c201", "c202", "c203", "c204", "c205"};
        for (String k : demo) {
            if (!existing.contains(k)) {
                gateway.save(new SavedRecipe(USER_ID, k));
            }
        }
    }
}
