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

        // 1) Initial load (in case user is already logged in)
        Long initialUserId = viewManagerModel.getCurrentUserId();
        if (initialUserId != null) {
            savedController.executeRetrieve(initialUserId);
        }

// 2) Every time this view becomes active, reload the list
        this.viewManagerModel.addPropertyChangeListener(evt -> {
            if ("activeView".equals(evt.getPropertyName())
                    && SavedRecipeViewModel.VIEW_NAME.equals(evt.getNewValue())) {
                Long userId = viewManagerModel.getCurrentUserId();
                if (userId != null) {
                    savedController.executeRetrieve(userId);
                }
            }
        });

        setPreferredSize(new Dimension(600, 400));
        setLayout(new BorderLayout(10, 10));

        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Saved Recipes"));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Left side: Refresh, View Details, Delete
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

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> viewManagerModel.setActiveViewName("home"));
        backPanel.add(backButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(backPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onRefresh(ActionEvent e) {
        Long userId = viewManagerModel.getCurrentUserId();
        savedController.executeRetrieve(userId);
    }

    private void onView(ActionEvent e) {
        int index = recipeList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a recipe first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String item = listModel.get(index);
        String recipeKey = parseRecipeKey(item);
        if (recipeKey == null || recipeKey.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Invalid recipe entry.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                // User chose "Open Recipe Details"
                viewRecipeController.execute(recipeKey);
                viewManagerModel.setActiveViewName(ViewRecipeNoSave.VIEW_NAME);
            } else {
                // Cancel or closed dialog -> do nothing
                return;
            }
        } else {
            // API recipe ("a" + id) — same behaviour as before
            viewRecipeController.execute(recipeKey);
            viewManagerModel.setActiveViewName(ViewRecipeNoSave.VIEW_NAME);
        }
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
        // Only react to state changes
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

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
