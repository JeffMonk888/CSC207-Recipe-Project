package demo;

import data.category.InMemoryCategoryGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Category;
import domain.entity.SavedRecipe;
import usecase.category.CategoryDataAccessInterface;
import usecase.category.assign_category.*;
import usecase.category.create_category.*;
import usecase.category.filter_by_category.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDemo {

    private static final long USER_ID = 1L;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CategoryDemo::createAndShow);
    }

    private static void createAndShow() {

        UserSavedRecipeAccessObject savedGateway =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");
        CategoryDataAccessInterface categoryGateway = new InMemoryCategoryGateway();

        // Seed demo recipes + categories/assignments
        seedDemoData(savedGateway, categoryGateway);

        // ==== presenters ====
        CreateCategoryOutputBoundary createPresenter = new CreateCategoryOutputBoundary() {
            @Override
            public void presentSuccess(CreateCategoryOutputData outputData) {
                System.out.println("[UC10] Created category: " + outputData.getCategory());
            }

            @Override
            public void presentFailure(String msg) {
                JOptionPane.showMessageDialog(null, msg, "Category Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        AssignCategoryOutputBoundary assignPresenter = new AssignCategoryOutputBoundary() {
            @Override
            public void presentSuccess(AssignCategoryOutputData outputData) {
                System.out.println("[UC10] Assigned: " + outputData.getAssignedRecipeIds());
            }

            @Override
            public void presentFailure(String msg) {
                JOptionPane.showMessageDialog(null, msg, "Category Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        FilterByCategoryOutputBoundary filterPresenter = new FilterByCategoryOutputBoundary() {
            @Override
            public void presentSuccess(FilterByCategoryOutputData outputData) {
                System.out.println("[UC10] Filter size=" + outputData.getSavedRecipes().size());
            }

            @Override
            public void presentFailure(String msg) {
                JOptionPane.showMessageDialog(null, msg, "Category Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // ==== interactors ====
        CreateCategoryInputBoundary createInteractor =
                new CreateCategoryInteractor(categoryGateway, createPresenter);
        AssignCategoryInputBoundary assignInteractor =
                new AssignCategoryInteractor(categoryGateway, assignPresenter);
        FilterByCategoryInputBoundary filterInteractor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, filterPresenter);

        // ==== window ====
        JFrame frame = new JFrame("Category (UC10)");
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(root);

        // Left: saved recipes
        DefaultListModel<String> recipeModel = new DefaultListModel<>();
        JList<String> recipeList = new JList<>(recipeModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane recipeScroll = new JScrollPane(recipeList);
        recipeScroll.setBorder(BorderFactory.createTitledBorder("Saved Recipes (user " + USER_ID + ")"));
        recipeScroll.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
        recipeScroll.setPreferredSize(new Dimension(260, 400));

        root.add(recipeScroll, BorderLayout.WEST);

        Runnable refreshRecipes = () -> {
            recipeModel.clear();
            for (SavedRecipe sr : savedGateway.findByUserId(USER_ID)) {
                recipeModel.addElement(sr.getRecipeKey());
            }
        };
        refreshRecipes.run();

        // Right: categories + buttons
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 20, 10, 10)); // left margin

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Category");
        JButton assignBtn = new JButton("Assign Recipe");
        JButton filterBtn = new JButton("Filter");
        JButton clearBtn = new JButton("Clear Filter");
        btnPanel.add(addBtn);
        btnPanel.add(assignBtn);
        btnPanel.add(filterBtn);
        btnPanel.add(clearBtn);

        DefaultListModel<String> categoryModel = new DefaultListModel<>();
        JList<String> categoryList = new JList<>(categoryModel);
        JScrollPane categoryScroll = new JScrollPane(categoryList);
        categoryScroll.setPreferredSize(new Dimension(280, 260));
        categoryScroll.setBorder(BorderFactory.createTitledBorder("Categories"));
        categoryScroll.setViewportBorder(new EmptyBorder(5, 5, 5, 5));

        // Button actions
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Category name:");
            if (name != null) {
                createInteractor.execute(new CreateCategoryInputData(USER_ID, name));
                categoryModel.clear();
                categoryGateway.findCategoriesForUser(USER_ID)
                        .forEach(c -> categoryModel.addElement(c.getId() + " - " + c.getName()));
            }
        });

        assignBtn.addActionListener(e -> {
            int rIdx = recipeList.getSelectedIndex();
            int cIdx = categoryList.getSelectedIndex();
            if (rIdx < 0 || cIdx < 0) {
                JOptionPane.showMessageDialog(frame, "Select recipe and category first.");
                return;
            }
            long recipeId = Long.parseLong(recipeModel.get(rIdx));
            long categoryId = Long.parseLong(categoryModel.get(cIdx).split("-")[0].trim());

            List<Long> ids = new ArrayList<>();
            ids.add(recipeId);

            assignInteractor.execute(
                    new AssignCategoryInputData(USER_ID, categoryId, ids));
        });

        filterBtn.addActionListener(e -> {
            int cIdx = categoryList.getSelectedIndex();
            if (cIdx < 0) {
                JOptionPane.showMessageDialog(frame, "Select a category first.");
                return;
            }
            long categoryId = Long.parseLong(categoryModel.get(cIdx).split("-")[0].trim());

            filterInteractor.execute(new FilterByCategoryInputData(USER_ID, categoryId));

            List<Long> ids = categoryGateway.getRecipeIdsForCategory(USER_ID, categoryId);
            recipeModel.clear();
            for (Long id : ids) {
                recipeModel.addElement(id.toString());
            }
        });

        clearBtn.addActionListener(e -> refreshRecipes.run());

        right.add(btnPanel);
        right.add(Box.createRigidArea(new Dimension(0, 10)));
        right.add(categoryScroll);
        right.add(Box.createVerticalGlue());

        root.add(right, BorderLayout.CENTER);

        // Populate initial category list (Italian / Chinese)
        categoryModel.clear();
        categoryGateway.findCategoriesForUser(USER_ID)
                .forEach(c -> categoryModel.addElement(c.getId() + " - " + c.getName()));

        frame.setVisible(true);
    }

    /**
     * Seed:
     *  - 5 recipes: 201, 202, 203, 204, 205
     *  - Italian: 201, 202
     *  - Chinese: 203, 204
     *  - 205 has no category
     */
    private static void seedDemoData(UserSavedRecipeAccessObject savedGateway,
                                     CategoryDataAccessInterface categoryGateway) {

        // 1) Ensure the 5 recipes exist in storage
        java.util.List<SavedRecipe> current = savedGateway.findByUserId(USER_ID);
        Set<String> existingKeys = current.stream()
                .map(SavedRecipe::getRecipeKey)
                .collect(Collectors.toSet());

        String[] neededKeys = {"201", "202", "203", "204", "205"};
        boolean addedAny = false;
        for (String key : neededKeys) {
            if (!existingKeys.contains(key)) {
                SavedRecipe sr = new SavedRecipe(USER_ID, key);
                sr.setFavourite(false);
                savedGateway.save(sr);
                addedAny = true;
            }
        }
        if (addedAny) {
            System.out.println("[Seed] Ensured demo recipes 201–205 exist for user " + USER_ID);
        }

        // 2) Create Italian and Chinese categories in the in-memory gateway
        Category italian = categoryGateway.createCategory(USER_ID, "Italian");
        Category chinese = categoryGateway.createCategory(USER_ID, "Chinese");

        // 3) Assign recipes:
        // Italian: 201, 202
        // Chinese: 203, 204
        List<Long> italianIds = new ArrayList<>();
        List<Long> chineseIds = new ArrayList<>();

        // we only assign IDs that actually exist in saved recipes
        Set<String> keysNow = savedGateway.findByUserId(USER_ID).stream()
                .map(SavedRecipe::getRecipeKey)
                .collect(Collectors.toSet());

        if (keysNow.contains("201")) italianIds.add(201L);
        if (keysNow.contains("202")) italianIds.add(202L);
        if (keysNow.contains("203")) chineseIds.add(203L);
        if (keysNow.contains("204")) chineseIds.add(204L);
        // 205 intentionally left unassigned

        if (!italianIds.isEmpty()) {
            categoryGateway.assignRecipesToCategory(USER_ID, italian.getId(), italianIds);
        }
        if (!chineseIds.isEmpty()) {
            categoryGateway.assignRecipesToCategory(USER_ID, chinese.getId(), chineseIds);
        }

        System.out.println("[Seed] Italian -> " + italianIds + ", Chinese -> " + chineseIds);
    }
}
