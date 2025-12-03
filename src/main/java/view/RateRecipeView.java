package view;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import interfaceadapter.rate_recipe.RateRecipeController;
import interfaceadapter.rate_recipe.RateRecipeState;
import interfaceadapter.rate_recipe.RateRecipeAbstractViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * GUI view for UC9: Rate Recipe.
 *
 * Features:
 *  - Shows saved recipes for a given userId in a list
 *  - Allows selecting a recipe and setting a star rating (0.0–5.0 step 0.5)
 *  - Allows clearing the rating for the selected recipe
 *
 * This view talks to the RateRecipeController and RateRecipeViewModel
 * (Clean Architecture: view -> controller -> use case -> gateway).
 *
 * NOTE:
 *  This class does NOT generate any demo data. It only reads saved recipes
 *  from the provided UserSavedRecipeAccessObject. Demo seeding logic lives
 *  in the demo package.
 */
public class RateRecipeView extends JFrame {

    private final long userId;

    /**
     * Read-only access to saved recipes for populating the list.
     * The write operations (updating rating) go through the use case.
     */
    private final UserSavedRecipeAccessObject savedRecipeGateway;

    private final RateRecipeController controller;
    private final RateRecipeAbstractViewModel viewModel;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipeList = new JList<>(listModel);
    private final JSpinner spinner;

    public RateRecipeView(RateRecipeController controller,
                          RateRecipeAbstractViewModel viewModel,
                          UserSavedRecipeAccessObject savedRecipeGateway,
                          long userId) {
        super("Rate Recipe");

        this.controller = controller;
        this.viewModel = viewModel;
        this.savedRecipeGateway = savedRecipeGateway;
        this.userId = userId;

        // Subscribe to ViewModel changes
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

                if (state.getStars() != null) {
                    spinner.setValue(state.getStars());
                }

                if (state.getErrorMessage() == null) {
                    refreshList();
                }
            }
        });

        // UI components
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // Left: saved recipes
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Saved Recipes (user " + userId + ")"));
        scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.setPreferredSize(new Dimension(260, 360));
        root.add(scrollPane, BorderLayout.WEST);

        // Right: rating controls
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

        // Save button -> goes through controller
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

            controller.rate(userId, recipeId, stars);
        });

        // Clear button -> goes through controller
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
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        refreshList();
    }

    /**
     * Reloads the list of saved recipes and their ratings into the left JList.
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

    /**
     * Extract the recipeId from a list entry "<recipeId>  <rating>".
     */
    private String extractRecipeId(String listEntry) {
        int spaceIndex = listEntry.indexOf(' ');
        if (spaceIndex == -1) {
            return listEntry;
        }
        return listEntry.substring(0, spaceIndex).trim();
    }
}
