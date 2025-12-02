package demo;

import view.RateRecipeView;

import javax.swing.*;

/**
 * Demo entry point for UC9 Rate Recipe view.
 *
 * This class only creates and shows the RateRecipeView.
 */
public class RateRecipeDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RateRecipeView view = new RateRecipeView();
            view.setVisible(true);
        });
    }
}
