package view;

import interface_adapter.saved_recipe.SavedRecipeController;
import interface_adapter.saved_recipe.SavedRecipeState;
import interface_adapter.saved_recipe.SavedRecipeViewModel;
import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.ViewManagerModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import demo.CategoryDemo;
import data.saved_recipe.UserSavedRecipeAccessObject;
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipePresenter;
import interface_adapter.rate_recipe.RateRecipeViewModel;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;

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

        setLayout(new BorderLayout());

        // ----- Top label -----
        JLabel titleLabel = new JLabel("My Saved Recipes");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // ----- Center list of saved recipes -----
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saved Recipes"));
        add(scrollPane, BorderLayout.CENTER);

        // ----- Bottom buttons (Refresh/View/Delete + Back) -----
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh");
        JButton viewButton = new JButton("View Details");
        JButton deleteButton = new JButton("Delete");

        refreshButton.addActionListener(this::onRefresh);
        viewButton.addActionListener(this::onView);
        deleteButton.addActionListener(this::onDelete);

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> viewManagerModel.setActiveViewName("home"));
        backPanel.add(backButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(backPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Right-side panel for extra actions (Rating & Category demo) ---
        JPanel demoPanel = new JPanel();
        demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
        demoPanel.setBorder(BorderFactory.createTitledBorder("Extra"));

        // Buttons
        JButton rateDemoButton = new JButton("Rate Recipe");
        JButton categoryDemoButton = new JButton("Category ");

        // Now use the real RateRecipeView instead of the demo
        rateDemoButton.addActionListener(e -> {
            // Open rating window using current user and real CSV data
            openRateRecipeWindow();
        });

        // Category demo unchanged
        categoryDemoButton.addActionListener(e -> {
            // Run the Category demo in its own window
            CategoryDemo.main(new String[0]);
        });

        // Layout: push them to the top or center as you like
        demoPanel.add(Box.createVerticalStrut(10));
        demoPanel.add(rateDemoButton);
        demoPanel.add(Box.createVerticalStrut(10));
        demoPanel.add(categoryDemoButton);
        demoPanel.add(Box.createVerticalGlue());

        // Add the panel on the right side of SavedRecipesView
        this.add(demoPanel, BorderLayout.EAST);
    }

    /**
     * Open the real RateRecipeView (not the demo) for the current logged-in user.
     * It uses the same CSV file as the saved-recipe use cases.
     */
    private void openRateRecipeWindow() {
        Long userIdObj = viewManagerModel.getCurrentUserId();
        if (userIdObj == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "You must be logged in to rate recipes.",
                    "No User",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        long userId = userIdObj;

        // Use the same CSV file as the rest of the app
        UserSavedRecipeAccessObject gateway =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // Wire up the rate-recipe use case
        RateRecipeViewModel rateViewModel = new RateRecipeViewModel();
        RateRecipeOutputBoundary presenter =
                new RateRecipePresenter(rateViewModel, viewManagerModel);
        RateRecipeInputBoundary interactor =
                new RateRecipeInteractor(gateway, presenter);
        RateRecipeController controller =
                new RateRecipeController(interactor);

        // Pass the actual userId so RateRecipeView shows this user's saved recipes
        RateRecipeView rateView =
                new RateRecipeView(controller, rateViewModel, gateway, userId);
        rateView.setVisible(true);
    }

    private void onRefresh(ActionEvent e) {
        Long userId = viewManagerModel.getCurrentUserId();
        savedController.executeRetrieve(userId);
    }

    private void onView(ActionEvent e) {
        int index = recipeList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a recipe first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String item = listModel.get(index);
        String recipeKey = parseRecipeKey(item);
        if (recipeKey == null || recipeKey.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid recipe entry.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // --- NEW LOGIC: Different behaviour for user-made vs API recipes ---

        if (recipeKey.startsWith("c")) {
            // This is a USER-MADE recipe (custom recipe: "c" + numericId)
            Object[] options = {"Open Recipe Details", "Cancel"};

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "This is a recipe you created.\nWhat would you like to do?",
                    "My Recipe",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == JOptionPane.OK_OPTION) {
                // Go to the view-recipe screen without calling the API
                // The interactor will detect "c..." and load from saved recipes
                viewRecipeController.execute(recipeKey);
                viewManagerModel.setActiveViewName("view_recipe_no_save");
            }
        } else {
            // This is an API-based recipe (numeric Spoonacular ID)
            // We call the view-recipe use case as usual, which uses the API/DAO.
            viewRecipeController.execute(recipeKey);
            viewManagerModel.setActiveViewName("view_recipe");
        }
    }

    private void onDelete(ActionEvent e) {
        int index = recipeList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a recipe first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Long userId = viewManagerModel.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No logged-in user. Please log in again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String item = listModel.get(index);
        String recipeKey = parseRecipeKey(item);
        if (recipeKey == null || recipeKey.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid recipe entry.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete recipe " + recipeKey + "?",
                "Confirm Delete",
                JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            savedController.executeDelete(userId, recipeKey);
            savedController.executeRetrieve(userId); // refresh after delete
        }
    }

    /**
     * Presenter formats each row as "<key> - <title>".
     * This returns the key part (before " - ").
     */
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
        SavedRecipeState state = viewModel.getState();
        if (state == null) {
            return; // nothing to display yet
        }

        // Show any error message
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }

        // Populate the center list with saved recipes
        listModel.clear();
        for (String recipeStr : state.getSavedRecipes()) {
            listModel.addElement(recipeStr);
        }
    }
}
