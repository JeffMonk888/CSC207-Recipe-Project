package view;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import usecase.rate_recipe.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * GUI view for UC9: Rate Recipe.
 *
 * Features:
 *  - Shows saved recipes for USER_ID in a list
 *  - Allows selecting a recipe and setting a star rating (0.0–5.0 step 0.5)
 *  - Allows clearing the rating for the selected recipe
 *
 * This view talks directly to the rate-recipe use case and its gateway.
 */
public class RateRecipeView extends JFrame {

    private static final long USER_ID = 1L;

    private final UserSavedRecipeAccessObject gateway;
    private final RateRecipeInputBoundary interactor;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipeList = new JList<>(listModel);
    private final JSpinner spinner;

    public RateRecipeView() {
        super("Rate Recipe");

        // ===== Gateway =====
        this.gateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // Seed demo data if this user has no saved recipes yet
        seedDemoData(gateway);

        // ===== Presenter =====
        RateRecipeOutputBoundary presenter = new RateRecipeOutputBoundary() {
            @Override
            public void presentSuccess(RateRecipeOutputData outputData) {
                if (outputData.isRemoved()) {
                    System.out.println("[UC9] Rating cleared.");
                } else {
                    System.out.println("[UC9] Rating saved: " + outputData.getRating());
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                JOptionPane.showMessageDialog(RateRecipeView.this,
                        errorMessage,
                        "Rating Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };

        // ===== Interactor =====
        this.interactor = new RateRecipeInteractor(gateway, presenter);

        // ===== UI components =====
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
                BorderFactory.createTitledBorder("Saved Recipes (user " + USER_ID + ")"));
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

        // ===== Bigger spinner for stars =====
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 5.0, 0.5);
        spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(80, 32));

        // Enlarge font inside the spinner editor
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setColumns(3);
            tf.setFont(tf.getFont().deriveFont(Font.BOLD, 16f));
            tf.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JButton save = new JButton("Save Rating");
        JButton clear = new JButton("Clear Rating");

        // Save button
        save.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(RateRecipeView.this,
                        "Select a recipe first.");
                return;
            }

            String recipeId = listModel.get(index).split("\\s+")[0];
            double stars = (double) spinner.getValue();

            interactor.execute(RateRecipeInputData.forRating(USER_ID, recipeId, stars));
            refreshList();
        });

        // Clear button
        clear.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(RateRecipeView.this,
                        "Select a recipe first.");
                return;
            }

            String recipeId = listModel.get(index).split("\\s+")[0];
            interactor.execute(RateRecipeInputData.forClear(USER_ID, recipeId));
            refreshList();
        });

        ratingPanel.add(new JLabel("Stars:"));
        ratingPanel.add(spinner);
        ratingPanel.add(save);
        ratingPanel.add(clear);

        right.add(ratingPanel);
        right.add(Box.createVerticalGlue());
        root.add(right, BorderLayout.CENTER);

        // Initial load
        refreshList();
    }

    /**
     * Reloads the list of saved recipes and their ratings into the left JList.
     */
    private void refreshList() {
        listModel.clear();
        for (SavedRecipe sr : gateway.findByUserId(USER_ID)) {
            String recipeId = sr.getRecipeKey();
            UserRating rating = gateway.findByUserAndRecipe(USER_ID, recipeId);
            String ratingStr = (rating == null ? "(no rating)" : rating.getStars() + "★");
            listModel.addElement(recipeId + "  " + ratingStr);
        }
    }

    /**
     * Seed two demo recipes for USER_ID if none exist yet.
     * We use numeric keys so UC9 (which expects long recipeId) can parse them.
     */
    private static void seedDemoData(UserSavedRecipeAccessObject gateway) {
        if (!gateway.findByUserId(USER_ID).isEmpty()) {
            return; // already has data, do not duplicate
        }

        SavedRecipe r1 = new SavedRecipe(USER_ID, "101");
        r1.setFavourite(false);
        gateway.save(r1);

        SavedRecipe r2 = new SavedRecipe(USER_ID, "102");
        r2.setFavourite(false);
        gateway.save(r2);

        System.out.println("[Seed] Added demo recipes 101 and 102 for user " + USER_ID);
    }
}
