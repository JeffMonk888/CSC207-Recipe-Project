package view;

import data.category.InMemoryCategoryGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Category;
import domain.entity.SavedRecipe;
import interface_adapter.category.CategoryController;
import interface_adapter.category.CategoryState;
import interface_adapter.category.CategoryViewModel;
import interface_adapter.category.CategoryPresenter;
import usecase.category.CategoryDataAccessInterface;
import usecase.category.assign_category.AssignCategoryInteractor;
import usecase.category.create_category.CreateCategoryInteractor;
import usecase.category.delete_category.DeleteCategoryInteractor;
import usecase.category.filter_by_category.FilterByCategoryInteractor;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInteractor;
import usecase.common.MotionForRecipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * GUI view for UC10 Category.
 *
 * This version uses:
 *  - CategoryController
 *  - CategoryViewModel
 *  - CategoryPresenter
 *
 * The view never calls the interactors directly anymore.
 */
public class CategoryView extends JFrame implements PropertyChangeListener {

    /** For now we keep a fixed demo user id. */
    private static final long USER_ID = 1L;

    // ==== Gateways ====
    private final CategoryDataAccessInterface categoryGateway;
    private final MotionForRecipe savedGateway;

    // ==== MVC: controller + view model ====
    private final CategoryViewModel categoryViewModel;
    private final CategoryController categoryController;

    // ==== Swing models ====
    private final DefaultListModel<String> savedModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryRecipeModel = new DefaultListModel<>();

    private final JList<String> savedList = new JList<>(savedModel);
    private final JList<String> categoryList = new JList<>(categoryModel);
    private final JList<String> categoryRecipeList = new JList<>(categoryRecipeModel);

    private final JTextField newCategoryField = new JTextField(12);

    /** Whether the bottom panel is currently filtered by a category. */
    private boolean isFiltered = false;

    public CategoryView() {
        super("Category Demo");

        // ===== Gateways =====
        this.categoryGateway = new InMemoryCategoryGateway();
        this.savedGateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // ===== ViewModel & Presenter & Interactors & Controller =====
        this.categoryViewModel = new CategoryViewModel();
        this.categoryViewModel.addPropertyChangeListener(this);

        CategoryPresenter presenter = new CategoryPresenter(categoryViewModel);

        // All category use cases share the same presenter.
        CreateCategoryInteractor createInteractor =
                new CreateCategoryInteractor(categoryGateway, presenter);
        AssignCategoryInteractor assignInteractor =
                new AssignCategoryInteractor(categoryGateway, presenter);
        FilterByCategoryInteractor filterInteractor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, presenter);
        RemoveRecipeFromCategoryInteractor removeInteractor =
                new RemoveRecipeFromCategoryInteractor(categoryGateway, presenter);
        DeleteCategoryInteractor deleteInteractor =
                new DeleteCategoryInteractor(categoryGateway, presenter);

        this.categoryController = new CategoryController(
                createInteractor,
                deleteInteractor,
                assignInteractor,
                filterInteractor,
                removeInteractor
        );

        // Build the UI
        buildLayout();

        // Initial data load
        refreshSavedList();
        loadInitialCategoriesFromGateway();
    }

    // ===================== UI layout =====================

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
        JPanel bottom = new JPanel(new GridLayout(4, 1, 5, 5));

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

        // Row 4: back button
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Saved Recipes");
        row4.add(backButton);

        bottom.add(row1);
        bottom.add(row2);
        bottom.add(row3);
        bottom.add(row4);

        root.add(bottom, BorderLayout.SOUTH);

        // ===== Button actions =====

        // Refresh saved recipes (left list)
        refreshSavedButton.addActionListener(e -> refreshSavedList());

        // Refresh categories (reload from gateway into ViewModel)
        refreshCategoriesButton.addActionListener(e -> loadInitialCategoriesFromGateway());

        // Create category -> controller + presenter + view model
        createButton.addActionListener(e -> {
            String name = newCategoryField.getText().trim();
            categoryController.createCategory(USER_ID, name);
            // We rely on presenter + ViewModel to trigger UI update.
        });

        // Delete category
        deleteButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(this,
                        "Select a category first.", "Delete Category",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            categoryController.deleteCategory(USER_ID, cid);
            // presenter will update state and clear filtered list if needed
        });

        // Assign saved recipe to category
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

            categoryController.assignRecipesToCategory(
                    USER_ID, cid, Collections.singletonList(recipeId)
            );

            // If currently filtered, re-apply filter to reflect new assignment
            refreshFilteredRecipes();
        });

        // Remove recipe from category
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

            categoryController.removeRecipeFromCategory(USER_ID, cid, recipeId);

            // If currently filtered, re-apply filter to reflect removal
            refreshFilteredRecipes();
        });

        // Filter by category
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

        // Clear filter
        clearFilterButton.addActionListener(e -> {
            isFiltered = false;
            CategoryState state = categoryViewModel.getState();
            state.setFilteredRecipes(List.of());
            categoryViewModel.fireStateChanged();
        });

        // Back button (for demo we just close this window)
        backButton.addActionListener(e -> dispose());
    }

    // ===================== ViewModel listener =====================

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != categoryViewModel) {
            return;
        }

        CategoryState state = categoryViewModel.getState();

        // Update category list
        categoryModel.clear();
        for (Category c : state.getCategories()) {
            categoryModel.addElement(c.getId() + " - " + c.getName());
        }

        // Update filtered recipes list
        categoryRecipeModel.clear();
        for (SavedRecipe sr : state.getFilteredRecipes()) {
            String text = sr.getRecipeKey();
            if (sr.isFavourite()) {
                text += " ★";
            }
            categoryRecipeModel.addElement(text);
        }

        // Show messages if any
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getErrorMessage(),
                    "Category Error",
                    JOptionPane.ERROR_MESSAGE);
        } else if (state.getMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getMessage(),
                    "Category Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===================== Helper methods =====================

    /**
     * Refreshes the left list of saved recipes for the current user.
     * This still uses the MotionForRecipe gateway directly.
     */
    private void refreshSavedList() {
        savedModel.clear();
        for (SavedRecipe sr : savedGateway.findByUserId(USER_ID)) {
            String text = sr.getRecipeKey();
            if (sr.isFavourite()) {
                text += " ★";
            }
            savedModel.addElement(text);
        }
    }

    /**
     * Loads all categories for the current user from gateway into the ViewModel,
     * then fires a state change so the UI is updated.
     */
    private void loadInitialCategoriesFromGateway() {
        List<Category> categories = categoryGateway.findCategoriesForUser(USER_ID);
        CategoryState state = categoryViewModel.getState();
        state.setCategories(categories);
        categoryViewModel.fireStateChanged();
    }

    /**
     * If we are currently filtered, re-runs the filter use case
     * via the controller.
     */
    private void refreshFilteredRecipes() {
        if (!isFiltered) {
            return;
        }
        Long cid = getSelectedCategoryId(categoryList);
        if (cid == null) {
            CategoryState state = categoryViewModel.getState();
            state.setFilteredRecipes(List.of());
            categoryViewModel.fireStateChanged();
            return;
        }
        categoryController.filterByCategory(USER_ID, cid);
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
     * Extracts leading token: "c201 ★" → "c201".
     */
    private static String parseLeadingToken(String line) {
        if (line == null) return "";
        line = line.trim();
        int space = line.indexOf(" ");
        return space < 0 ? line : line.substring(0, space);
    }
}
