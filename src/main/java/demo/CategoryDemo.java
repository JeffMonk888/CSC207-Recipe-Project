package demo;

import view.CategoryView;

import javax.swing.*;

/**
 * Demo entry point for UC10 Category view.
 *
 * This class only creates and shows the CategoryView.
 */
public class CategoryDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CategoryView view = new CategoryView();
            view.setVisible(true);
        });
    }
}
