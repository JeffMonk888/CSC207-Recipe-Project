package demo;

import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import usecase.rate_recipe.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RateRecipeDemo {

    private static final long USER_ID = 1L;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RateRecipeDemo::createAndShow);
    }

    private static void createAndShow() {

        UserSavedRecipeAccessObject gateway =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // Seed demo data if this user has no saved recipes yet
        seedDemoData(gateway);

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
                JOptionPane.showMessageDialog(null, errorMessage, "Rating Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        RateRecipeInputBoundary interactor = new RateRecipeInteractor(gateway, presenter);

        // ================= UI ==================
        JFrame frame = new JFrame("Rate Recipe (UC9)");
        frame.setSize(550, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));    // overall padding
        frame.setContentPane(root);

        // Left: Saved Recipes list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saved Recipes (user " + USER_ID + ")"));
        scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.setPreferredSize(new Dimension(260, 340));
        root.add(scrollPane, BorderLayout.WEST);

        // Load recipes
        Runnable refresh = () -> {
            listModel.clear();
            for (SavedRecipe sr : gateway.findByUserId(USER_ID)) {
                long recipeId = Long.parseLong(sr.getRecipeKey());
                UserRating rating = gateway.findByUserAndRecipe(USER_ID, recipeId);
                String ratingStr = (rating == null ? "(no rating)" : rating.getStars() + "★");
                listModel.addElement(recipeId + "  " + ratingStr);
            }
        };
        refresh.run();

        // Right: Rating panel
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 20, 10, 10));   // left margin so it doesn't hug window edge

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        ratingPanel.setBorder(BorderFactory.createTitledBorder("Rating (UC9)"));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 5.0, 0.5);
        JSpinner spinner = new JSpinner(spinnerModel);

        JButton save = new JButton("Save Rating");
        JButton clear = new JButton("Clear Rating");

        save.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(frame, "Select a recipe first.");
                return;
            }

            long recipeId = Long.parseLong(listModel.get(index).split("\\s+")[0]);
            double stars = (double) spinner.getValue();

            interactor.execute(RateRecipeInputData.forRating(USER_ID, recipeId, stars));
            refresh.run();
        });

        clear.addActionListener(e -> {
            int index = recipeList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(frame, "Select a recipe first.");
                return;
            }

            long recipeId = Long.parseLong(listModel.get(index).split("\\s+")[0]);
            interactor.execute(RateRecipeInputData.forClear(USER_ID, recipeId));
            refresh.run();
        });

        ratingPanel.add(new JLabel("Stars:"));
        ratingPanel.add(spinner);
        ratingPanel.add(save);
        ratingPanel.add(clear);

        right.add(ratingPanel);
        right.add(Box.createVerticalGlue());
        root.add(right, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    /**
     * Seed 2 demo recipes for USER_ID if none exist yet.
     * We use numeric keys so UC9 (which expects long recipeId) can parse them.
     */
    private static void seedDemoData(UserSavedRecipeAccessObject gateway) {
        if (!gateway.findByUserId(USER_ID).isEmpty()) {
            return; // already has data, don't duplicate
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
