package view;

import data.category.InMemoryCategoryGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Category;
import domain.entity.SavedRecipe;
import usecase.category.CategoryDataAccessInterface;
import usecase.category.assign_category.*;
import usecase.category.create_category.*;
import usecase.category.delete_category.*;
import usecase.category.filter_by_category.*;
import usecase.category.remove_recipe.*;
import usecase.common.MotionForRecipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

/**
 * GUI view for UC10 Category.
 *
 * This class is responsible for displaying:
 *  - all saved recipes of user 1
 *  - category list
 *  - recipes in the selected category (filter result)
 *
 * It wires the UI directly to the use cases and gateways.
 */
public class CategoryView extends JFrame {

    private static final long USER_ID = 1L;

    // Gateways
    private final CategoryDataAccessInterface categoryGateway;
    private final MotionForRecipe savedGateway;

    // Swing models
    private final DefaultListModel<String> savedModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryRecipeModel = new DefaultListModel<>();

    private final JList<String> savedList = new JList<>(savedModel);
    private final JList<String> categoryList = new JList<>(categoryModel);
    private final JList<String> categoryRecipeList = new JList<>(categoryRecipeModel);

    private final JTextField newCategoryField = new JTextField(12);

    // Whether the bottom panel is currently filtered by a category
    private boolean isFiltered = false;

    // Use case boundaries
    private final CreateCategoryInputBoundary createInteractor;
    private final AssignCategoryInputBoundary assignInteractor;
    private final FilterByCategoryInputBoundary filterInteractor;
    private final RemoveRecipeFromCategoryInputBoundary removeInteractor;
    private final DeleteCategoryInputBoundary deleteInteractor;

    public CategoryView() {
        super("Category Demo");

        // ===== Gateways =====
        this.categoryGateway = new InMemoryCategoryGateway();
        this.savedGateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // Seed demo saved recipes if needed
        seedSavedRecipes(savedGateway);

        // ===== Presenters =====
        CreateCategoryOutputBoundary createPresenter = new CreateCategoryOutputBoundary() {
            @Override
            public void presentSuccess(CreateCategoryOutputData outputData) {
                JOptionPane.showMessageDialog(CategoryView.this,
                        "Category created: " + outputData.getCategory().getName(),
                        "Create Category", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void presentFailure(String errorMessage) {
                JOptionPane.showMessageDialog(CategoryView.this,
                        errorMessage,
                        "Create Category Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        AssignCategoryOutputBoundary assignPresenter = new AssignCategoryOutputBoundary() {
            @Override
            public void presentSuccess(AssignCategoryOutputData outputData) {
                JOptionPane.showMessageDialog(CategoryView.this,
                        "Assigned recipe(s) " + outputData.getAssignedRecipeIds()
                                + " to category " + outputData.getCategoryId(),
                        "Assign to Category", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void presentFailure(String errorMessage) {
                JOptionPane.showMessageDialog(CategoryView.this,
                        errorMessage,
                        "Assign Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        FilterByCategoryOutputBoundary filterPresenter = new FilterByCategoryOutputBoundary() {
            @Override
            public void presentSuccess(FilterByCategoryOutputData outputData) {
                categoryRecipeModel.clear();
                for (SavedRecipe sr : outputData.getSavedRecipes()) {
                    String text = sr.getRecipeKey();
                    if (sr.isFavourite()) text += " ★";
                    categoryRecipeModel.addElement(text);
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                JOptionPane.showMessageDialog(CategoryView.this, errorMessage,
                        "Filter Error", JOptionPane.ERROR_MESSAGE);
                categoryRecipeModel.clear();
            }
        };

        RemoveRecipeFromCategoryOutputBoundary removePresenter =
                new RemoveRecipeFromCategoryOutputBoundary() {
                    @Override
                    public void presentSuccess(RemoveRecipeFromCategoryOutputData outputData) {
                        JOptionPane.showMessageDialog(CategoryView.this,
                                "Removed recipe " + outputData.getRecipeId() +
                                        " from category " + outputData.getCategoryId(),
                                "Remove From Category",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    @Override
                    public void presentFailure(String errorMessage) {
                        JOptionPane.showMessageDialog(CategoryView.this,
                                errorMessage,
                                "Remove Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                };

        DeleteCategoryOutputBoundary deletePresenter =
                new DeleteCategoryOutputBoundary() {
                    @Override
                    public void presentSuccess(DeleteCategoryOutputData outputData) {
                        JOptionPane.showMessageDialog(CategoryView.this,
                                "Deleted category " + outputData.getDeletedCategoryId(),
                                "Delete Category", JOptionPane.INFORMATION_MESSAGE);
                    }

                    @Override
                    public void presentFailure(String errorMessage) {
                        JOptionPane.showMessageDialog(CategoryView.this,
                                errorMessage,
                                "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }
                };

        // ===== Interactors =====
        this.createInteractor =
                new CreateCategoryInteractor(categoryGateway, createPresenter);
        this.assignInteractor =
                new AssignCategoryInteractor(categoryGateway, assignPresenter);
        this.filterInteractor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, filterPresenter);
        this.removeInteractor =
                new RemoveRecipeFromCategoryInteractor(categoryGateway, removePresenter);
        this.deleteInteractor =
                new DeleteCategoryInteractor(categoryGateway, deletePresenter);

        // Build the UI
        buildLayout();

        // Initial data load
        refreshSavedList();
        refreshCategoryList();
    }

    private void buildLayout() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // ===== Left: saved recipes =====
        savedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane savedScroll = new JScrollPane(savedList);
        savedScroll.setBorder(BorderFactory.createTitledBorder("Saved Recipes"));

        JButton refreshSavedButton = new JButton("Refresh Saved");

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(savedScroll, BorderLayout.CENTER);
        leftPanel.add(refreshSavedButton, BorderLayout.SOUTH);

        root.add(leftPanel, BorderLayout.WEST);

        // ===== Right: categories (top) + recipes in category (bottom) =====
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane categoryScroll = new JScrollPane(categoryList);
        categoryScroll.setBorder(BorderFactory.createTitledBorder("Categories"));

        categoryRecipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane categoryRecipeScroll = new JScrollPane(categoryRecipeList);
        categoryRecipeScroll.setBorder(
                BorderFactory.createTitledBorder("Recipes (filtered / unfiltered)")
        );

        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, categoryScroll, categoryRecipeScroll
        );
        rightSplit.setResizeWeight(0.4);
        root.add(rightSplit, BorderLayout.CENTER);

        // ===== Bottom controls =====
        JPanel bottom = new JPanel(new GridLayout(3, 1, 5, 5));

        // Row 1: create / delete category
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("New category:"));
        row1.add(newCategoryField);
        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete selected category");
        JButton refreshCategoriesButton = new JButton("Refresh Categories");
        row1.add(createButton);
        row1.add(deleteButton);
        row1.add(refreshCategoriesButton);

        // Row 2: assign / remove
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assignButton = new JButton("Assign selected saved recipe to category");
        JButton removeButton = new JButton("Remove selected recipe from category");
        row2.add(assignButton);
        row2.add(removeButton);

        // Row 3: filter / clear filter
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton filterButton = new JButton("Filter by selected category");
        JButton clearFilterButton = new JButton("Clear Board");
        row3.add(filterButton);
        row3.add(clearFilterButton);

        bottom.add(row1);
        bottom.add(row2);
        bottom.add(row3);
        root.add(bottom, BorderLayout.SOUTH);

        // ===== Button actions =====

        refreshSavedButton.addActionListener(e -> refreshSavedList());
        refreshCategoriesButton.addActionListener(e -> refreshCategoryList());

        createButton.addActionListener(e -> {
            createInteractor.execute(
                    new CreateCategoryInputData(USER_ID, newCategoryField.getText().trim())
            );
            refreshCategoryList();
        });

        deleteButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(this,
                        "Select a category first.", "Delete Category",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteInteractor.execute(new DeleteCategoryInputData(USER_ID, cid));
            categoryRecipeModel.clear();
            refreshCategoryList();
        });

        assignButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            int idx = savedList.getSelectedIndex();
            if (cid == null || idx < 0) {
                JOptionPane.showMessageDialog(this,
                        "Select both a category and a saved recipe.",
                        "Assign Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String token = parseLeadingToken(savedModel.get(idx));
            String recipeId = token;

            assignInteractor.execute(
                    new AssignCategoryInputData(USER_ID, cid,
                            Collections.singletonList(recipeId))
            );
            refreshFilteredRecipes();
        });

        removeButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            int idx = categoryRecipeList.getSelectedIndex();
            if (cid == null || idx < 0) {
                JOptionPane.showMessageDialog(this,
                        "Select a category and a recipe in the bottom list.",
                        "Remove Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String token = parseLeadingToken(categoryRecipeModel.get(idx));
            String recipeId = token;

            removeInteractor.execute(
                    new RemoveRecipeFromCategoryInputData(USER_ID, cid, recipeId)
            );
            refreshFilteredRecipes();
        });

        filterButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(this,
                        "Select a category first.", "Filter",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            isFiltered = true;
            refreshFilteredRecipes();
        });

        clearFilterButton.addActionListener(e -> {
            isFiltered = false;
            categoryRecipeModel.clear();
        });
    }

    // ===== Helper methods =====

    private void refreshSavedList() {
        savedModel.clear();
        for (SavedRecipe sr : savedGateway.findByUserId(USER_ID)) {
            String text = sr.getRecipeKey();
            if (sr.isFavourite()) text += " ★";
            savedModel.addElement(text);
        }
    }

    private void refreshCategoryList() {
        categoryModel.clear();
        for (Category c : categoryGateway.findCategoriesForUser(USER_ID)) {
            categoryModel.addElement(c.getId() + " - " + c.getName());
        }
    }

    private void refreshFilteredRecipes() {
        if (!isFiltered) {
            return;
        }
        Long cid = getSelectedCategoryId(categoryList);
        if (cid == null) {
            categoryRecipeModel.clear();
            return;
        }
        filterInteractor.execute(new FilterByCategoryInputData(USER_ID, cid));
    }

    /**
     * Extracts category ID from a line such as "3 - Quick Meals".
     */
    private static Long getSelectedCategoryId(JList<String> list) {
        int idx = list.getSelectedIndex();
        if (idx < 0) return null;
        String line = list.getModel().getElementAt(idx);
        int dash = line.indexOf(" - ");
        String idStr = (dash >= 0 ? line.substring(0, dash) : line).trim();
        try {
            return Long.parseLong(idStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts leading token: "201 ★" → "201".
     */
    private static String parseLeadingToken(String line) {
        if (line == null) return "";
        line = line.trim();
        int space = line.indexOf(" ");
        return space < 0 ? line : line.substring(0, space);
    }

    /**
     * Ensures demo saved recipes (201–205) exist.
     */
    private static void seedSavedRecipes(MotionForRecipe gateway) {
        Set<String> existing = new HashSet<>();
        for (SavedRecipe sr : gateway.findByUserId(USER_ID)) {
            existing.add(sr.getRecipeKey());
        }
        String[] demo = {"201", "202", "203", "204", "205"};
        for (String k : demo) {
            if (!existing.contains(k)) {
                gateway.save(new SavedRecipe(USER_ID, k));
            }
        }
    }
}
