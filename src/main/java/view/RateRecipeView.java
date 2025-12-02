package view;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipeState;
import interface_adapter.rate_recipe.RateRecipeViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class RateRecipeView extends JFrame {

    // Use the actual logged-in user id (passed from constructor)
    private final long userId;

    private final UserSavedRecipeAccessObject savedRecipeGateway;
    private final RateRecipeController controller;
    private final RateRecipeViewModel viewModel;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipeList = new JList<>(listModel);
    private final JSpinner spinner;

    public RateRecipeView(RateRecipeController controller,
                          RateRecipeViewModel viewModel,
                          UserSavedRecipeAccessObject savedRecipeGateway,
                          long userId) {
        super("Rate Recipe");

        this.controller = controller;
        this.viewModel = viewModel;
        this.savedRecipeGateway = savedRecipeGateway;
        this.userId = userId;

        this.viewModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                RateRecipeState state = RateRecipeView.this.viewModel.getState();

                if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            RateRecipeView.this,
                            state.getErrorMessage(),
                            "Rating Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (state.getMessage() != null && !state.getMessage().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            RateRecipeView.this,
                            state.getMessage(),
                            "Rating Info",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                // Update spinner when state contains a star value
                if (state.getStars() != null) {
                    spinner.setValue(state.getStars());
                }

                // If no error, refresh list to reflect new rating
                if (state.getErrorMessage() == null) {
                    refreshList();
                }
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Saved Recipes (user " + userId + ")"));
        scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.setPreferredSize(new Dimension(260, 360));
        root.add(scrollPane, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 20, 10, 10));

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        ratingPanel.setBorder(BorderFactory.createTitledBorder("Rating"));
        ratingPanel.setPreferredSize(new Dimension(260, 200));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 5.0, 0.5);
        spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(80, 32));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setColumns(3);
            tf.setFont(tf.getFont().deriveFont(Font.BOLD, 16f));
            tf.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JButton save = new JButton("Save Rating");
        JButton clear = new JButton("Clear Rating");

        save.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(
                        RateRecipeView.this,
                        "Select a recipe first."
                );
                return;
            }

            String listEntry = listModel.get(index);
            String recipeId = extractRecipeId(listEntry);
            double stars = (double) spinner.getValue();

            // Use the actual userId instead of 1L
            controller.rate(userId, recipeId, stars);
        });

        clear.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(
                        RateRecipeView.this,
                        "Select a recipe first."
                );
                return;
            }

            String listEntry = listModel.get(index);
            String recipeId = extractRecipeId(listEntry);

            // Use the actual userId instead of 1L
            controller.clearRating(userId, recipeId);
        });

        ratingPanel.add(new JLabel("Stars:"));
        ratingPanel.add(spinner);
        ratingPanel.add(save);
        ratingPanel.add(clear);

        right.add(ratingPanel);
        right.add(Box.createVerticalGlue());
        root.add(right, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Saved Recipes");
        backButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        root.add(bottomPanel, BorderLayout.SOUTH);

        // Initial load
        refreshList();
    }

    /**
     * Refresh the left list using this user's saved recipes and ratings.
     */
    private void refreshList() {
        listModel.clear();
        List<SavedRecipe> savedRecipes = savedRecipeGateway.findByUserId(userId);

        for (SavedRecipe sr : savedRecipes) {
            String recipeId = sr.getRecipeKey();
            UserRating rating = savedRecipeGateway.findByUserAndRecipe(userId, recipeId);
            String ratingStr = (rating == null ? "(no rating)" : rating.getStars() + "★");
            listModel.addElement(recipeId + "  " + ratingStr);
        }
    }

    private String extractRecipeId(String listEntry) {
        int spaceIndex = listEntry.indexOf(' ');
        if (spaceIndex == -1) {
            return listEntry.trim();
        }
        return listEntry.substring(0, spaceIndex).trim();
    }
}
