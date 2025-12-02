package view;

import data.saved_recipe.UserSavedRecipeAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipePresenter;
import interface_adapter.rate_recipe.RateRecipeViewModel;
import interface_adapter.saved_recipe.SavedRecipeController;
import interface_adapter.saved_recipe.SavedRecipeState;
import interface_adapter.saved_recipe.SavedRecipeViewModel;
import interface_adapter.view_recipe.ViewRecipeController;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import demo.CategoryDemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SavedRecipesView extends JPanel implements PropertyChangeListener {

    private final SavedRecipeController savedController;
    private final SavedRecipeViewModel viewModel;
    private final ViewRecipeController viewRecipeController;
    private final ViewManagerModel viewManagerModel;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipeList = new JList<>(listModel);

    public SavedRecipesView(SavedRecipeController savedController,
                            SavedRecipeViewModel viewModel,
                            ViewRecipeController viewRecipeController,
                            ViewManagerModel viewManagerModel) {
        this.savedController = savedController;
        this.viewModel = viewModel;
        this.viewRecipeController = viewRecipeController;
        this.viewManagerModel = viewManagerModel;
        this.viewModel.addPropertyChangeListener(this);

        setPreferredSize(new Dimension(600, 400));
        setLayout(new BorderLayout(10, 10));

        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Saved Recipes"));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Left buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton viewButton = new JButton("View Details");
        JButton deleteButton = new JButton("Delete");

        refreshButton.addActionListener(this::onRefresh);
        viewButton.addActionListener(this::onView);
        deleteButton.addActionListener(this::onDelete);

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        // Right "Back to Home"
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> viewManagerModel.setActiveViewName("home"));
        backPanel.add(backButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(backPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Right side "Extra" panel
        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new BoxLayout(extraPanel, BoxLayout.Y_AXIS));
        extraPanel.setBorder(BorderFactory.createTitledBorder("Extra"));

        JButton rateButton = new JButton("Rate Recipe");
        JButton categoryButton = new JButton("Category");

        // === IMPORTANT: open real RateRecipeView for the CURRENT USER ===
        rateButton.addActionListener(e -> {
            Long currentUserId = viewManagerModel.getCurrentUserId();
            if (currentUserId == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No logged-in user found. Please sign in first."
                );
                return;
            }

            UserSavedRecipeAccessObject gateway =
                    new UserSavedRecipeAccessObject("user_recipe_links.csv");

            RateRecipeViewModel rateRecipeViewModel = new RateRecipeViewModel();
            RateRecipeOutputBoundary presenter =
                    new RateRecipePresenter(rateRecipeViewModel, viewManagerModel);
            RateRecipeInputBoundary interactor =
                    new RateRecipeInteractor(gateway, presenter);
            RateRecipeController controller = new RateRecipeController(interactor);

            RateRecipeView rateRecipeView =
                    new RateRecipeView(controller, rateRecipeViewModel, gateway, currentUserId);
            rateRecipeView.setVisible(true);
        });

        // Category demo stays unchanged
        categoryButton.addActionListener(e -> CategoryDemo.main(new String[0]));

        extraPanel.add(Box.createVerticalStrut(10));
        extraPanel.add(rateButton);
        extraPanel.add(Box.createVerticalStrut(10));
        extraPanel.add(categoryButton);
        extraPanel.add(Box.createVerticalGlue());

        add(extraPanel, BorderLayout.EAST);
    }

    private void onRefresh(ActionEvent e) {
        Long userId = viewManagerModel.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "No logged-in user.");
            return;
        }
        savedController.executeRetrieve(userId);
    }

    private void onView(ActionEvent e) {
        int index = recipeList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a recipe first.");
            return;
        }
        String item = listModel.get(index);
        String recipeKey = parseRecipeKey(item);
        if (recipeKey == null || recipeKey.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid recipe entry.");
            return;
        }
        viewRecipeController.execute(recipeKey);
    }

    private void onDelete(ActionEvent e) {
        int index = recipeList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a recipe first.");
            return;
        }
        String item = listModel.get(index);
        String recipeKey = parseRecipeKey(item);
        Long userId = viewManagerModel.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "No logged-in user.");
            return;
        }
        if (recipeKey == null || recipeKey.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid recipe entry.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete recipe " + recipeKey + "?",
                "Confirm Delete",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            savedController.executeDelete(userId, recipeKey);
            savedController.executeRetrieve(userId); // refresh
        }
    }

    private String parseRecipeKey(String line) {
        if (line == null) return null;
        int idx = line.indexOf(" - ");
        if (idx <= 0) return line.trim();
        return line.substring(0, idx).trim();
    }

    public String getViewName() {
        return SavedRecipeViewModel.VIEW_NAME;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        SavedRecipeState state = viewModel.getState();
        if (state == null) {
            return;
        }

        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }

        listModel.clear();
        for (String recipeStr : state.getSavedRecipes()) {
            listModel.addElement(recipeStr);
        }
    }
}
