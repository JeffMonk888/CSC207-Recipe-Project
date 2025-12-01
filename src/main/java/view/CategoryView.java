package view;

import data.category.InMemoryCategoryGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Category;
import domain.entity.SavedRecipe;
import interface_adapter.category.CategoryController;
import interface_adapter.category.CategoryPresenter;
import interface_adapter.category.CategoryState;
import interface_adapter.category.CategoryViewModel;
import usecase.category.CategoryDataAccessInterface;
import usecase.category.assign_category.AssignCategoryInputBoundary;
import usecase.category.assign_category.AssignCategoryInteractor;
import usecase.category.create_category.CreateCategoryInputBoundary;
import usecase.category.create_category.CreateCategoryInteractor;
import usecase.category.delete_category.DeleteCategoryInputBoundary;
import usecase.category.delete_category.DeleteCategoryInteractor;
import usecase.category.filter_by_category.FilterByCategoryInputBoundary;
import usecase.category.filter_by_category.FilterByCategoryInteractor;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInputBoundary;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInteractor;
import usecase.common.MotionForRecipe;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI view for UC10: Category management.
 *
 * This view uses the category interface adapters:
 *  - CategoryViewModel / CategoryState
 *  - CategoryController
 *  - CategoryPresenter (as the OutputBoundary implementation)
 *
 * The view directly reads from gateways to populate:
 *  - the list of saved recipes on the left
 *  - the list of categories in the middle
 * but all category-related operations (create, assign, filter, remove, delete)
 * go through the controller and use case interactors.
 */
public class CategoryView extends JFrame implements PropertyChangeListener {

    private static final long USER_ID = 1L;

    // Gateways
    private final CategoryDataAccessInterface categoryGateway;
    private final MotionForRecipe savedGateway;

    // Interface adapters
    private final CategoryViewModel viewModel;
    private final CategoryController controller;

    // Swing models and components
    private final DefaultListModel<String> savedModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> categoryRecipeModel = new DefaultListModel<>();

    private final JList<String> savedList = new JList<>(savedModel);
    private final JList<String> categoryList = new JList<>(categoryModel);
    private final JList<String> categoryRecipeList = new JList<>(categoryRecipeModel);

    private final JTextField newCategoryField = new JTextField(12);

    // Whether we are currently showing recipes filtered by a category
    private boolean isFiltered = false;

    /**
     * Construct a CategoryView wired with in-memory gateways and
     * all the necessary interface adapters (for demo use).
     */
    public CategoryView() {
        super("Category Management");

        // ===== Gateways =====
        this.categoryGateway = new InMemoryCategoryGateway();
        this.savedGateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");

        // ===== ViewModel & Presenter =====
        this.viewModel = new CategoryViewModel();
        CategoryPresenter presenter = new CategoryPresenter(viewModel);

        // ===== Interactors =====
        CreateCategoryInputBoundary createInteractor =
                new CreateCategoryInteractor(categoryGateway, presenter);
        AssignCategoryInputBoundary assignInteractor =
                new AssignCategoryInteractor(categoryGateway, presenter);
        FilterByCategoryInputBoundary filterInteractor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, presenter);
        RemoveRecipeFromCategoryInputBoundary removeInteractor =
                new RemoveRecipeFromCategoryInteractor(categoryGateway, presenter);
        DeleteCategoryInputBoundary deleteInteractor =
                new DeleteCategoryInteractor(categoryGateway, presenter);

        // ===== Controller =====
        this.controller = new CategoryController(
                createInteractor,
                assignInteractor,
                filterInteractor,
                removeInteractor,
                deleteInteractor
        );

        // Listen to ViewModel changes
        this.viewModel.addPropertyChangeListener(this);

        // Build UI and load initial data
        buildLayout();
        refreshSavedList();
        refreshCategoryList();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Builds the overall layout.
     *
     * Top: three columns (Saved, Categories, Recipes in Category).
     * Bottom: a horizontal row of buttons that is always visible.
     */
    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // ===== TOP PANEL: three columns with fixed preferred height =====
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setPreferredSize(new Dimension(900, 360));
        root.add(topPanel, BorderLayout.CENTER);

        // ----- LEFT COLUMN: Saved recipes -----
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createTitledBorder("Saved Recipes (user " + USER_ID + ")"));

        savedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane savedScroll = new JScrollPane(savedList);
        // Reduce preferred height so bottom controls have more room
        savedScroll.setPreferredSize(new Dimension(260, 260));
        left.add(savedScroll, BorderLayout.CENTER);

        JButton refreshSavedButton = new JButton("Refresh Saved");
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottom.add(refreshSavedButton);
        left.add(leftBottom, BorderLayout.SOUTH);

        topPanel.add(left);

        // ----- MIDDLE COLUMN: Categories -----
        JPanel middle = new JPanel(new BorderLayout(5, 5));
        middle.setBorder(BorderFactory.createTitledBorder("Categories"));

        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane categoryScroll = new JScrollPane(categoryList);
        // Also reduce height here so Create/Delete buttons are visible
        categoryScroll.setPreferredSize(new Dimension(260, 260));
        middle.add(categoryScroll, BorderLayout.CENTER);

        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createPanel.add(new JLabel("New Category:"));
        createPanel.add(newCategoryField);
        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshCategoriesButton = new JButton("Refresh Categories");
        createPanel.add(createButton);
        createPanel.add(deleteButton);
        createPanel.add(refreshCategoriesButton);
        middle.add(createPanel, BorderLayout.SOUTH);

        topPanel.add(middle);

        // ----- RIGHT COLUMN: Recipes in selected category -----
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createTitledBorder("Recipes in Selected Category"));

        categoryRecipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane categoryRecipeScroll = new JScrollPane(categoryRecipeList);
        categoryRecipeScroll.setPreferredSize(new Dimension(260, 260));
        right.add(categoryRecipeScroll, BorderLayout.CENTER);

        topPanel.add(right);

        // ===== BOTTOM PANEL: always-visible buttons =====
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        root.add(bottomPanel, BorderLayout.SOUTH);

        JButton assignButton = new JButton("Assign Selected Saved -> Category");
        JButton removeButton = new JButton("Remove Selected Recipe from Category");
        JButton filterButton = new JButton("Filter by Category");
        JButton clearFilterButton = new JButton("Clear Filter");
        JButton backButton = new JButton("Back / Close");

        bottomPanel.add(assignButton);
        bottomPanel.add(removeButton);
        bottomPanel.add(filterButton);
        bottomPanel.add(clearFilterButton);
        bottomPanel.add(backButton);

        // ===== Button actions =====

        refreshSavedButton.addActionListener(e -> refreshSavedList());
        refreshCategoriesButton.addActionListener(e -> refreshCategoryList());

        createButton.addActionListener(e -> {
            String name = newCategoryField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Category name cannot be empty.",
                        "Create Category Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            controller.createCategory(USER_ID, name);
            newCategoryField.setText("");
            refreshCategoryList();
        });

        deleteButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select a category to delete.",
                        "Delete Category",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            controller.deleteCategory(USER_ID, cid);
            refreshCategoryList();
            categoryRecipeModel.clear();
        });

        assignButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select a category in the middle list.",
                        "Assign Recipes",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            List<String> selectedRecipeIds = getSelectedRecipeKeys(savedList);
            if (selectedRecipeIds.isEmpty()) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select at least one saved recipe on the left.",
                        "Assign Recipes",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            controller.assignRecipesToCategory(USER_ID, cid, selectedRecipeIds);
            if (isFiltered) {
                refreshFilteredRecipes();
            }
        });

        removeButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select a category.",
                        "Remove Recipe",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            String recipeKey = getSelectedRecipeKey(categoryRecipeList);
            if (recipeKey == null) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select a recipe in the right-hand list.",
                        "Remove Recipe",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            controller.removeRecipeFromCategory(USER_ID, cid, recipeKey);
            refreshFilteredRecipes();
        });

        filterButton.addActionListener(e -> {
            Long cid = getSelectedCategoryId(categoryList);
            if (cid == null) {
                JOptionPane.showMessageDialog(
                        CategoryView.this,
                        "Please select a category to filter by.",
                        "Filter Recipes",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            isFiltered = true;
            refreshFilteredRecipes();
        });

        clearFilterButton.addActionListener(e -> {
            isFiltered = false;
            categoryRecipeModel.clear();
        });

        backButton.addActionListener(e -> dispose());
    }

    /**
     * Refreshes the left list of saved recipes from the gateway.
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
     * Refreshes the middle list of categories from the gateway.
     */
    private void refreshCategoryList() {
        categoryModel.clear();
        for (Category c : categoryGateway.findCategoriesForUser(USER_ID)) {
            categoryModel.addElement(c.getId() + " - " + c.getName());
        }
    }

    /**
     * Refreshes the right list of recipes in the selected category
     * by invoking the filter-by-category use case.
     */
    private void refreshFilteredRecipes() {
        if (!isFiltered) {
            categoryRecipeModel.clear();
            return;
        }
        Long cid = getSelectedCategoryId(categoryList);
        if (cid == null) {
            categoryRecipeModel.clear();
            return;
        }
        controller.filterRecipesByCategory(USER_ID, cid);
    }

    /**
     * Extracts the category id from a line like "3 - Dinner".
     */
    private static Long getSelectedCategoryId(JList<String> list) {
        int idx = list.getSelectedIndex();
        if (idx < 0) {
            return null;
        }
        String line = list.getModel().getElementAt(idx);
        int dash = line.indexOf(" - ");
        if (dash < 0) {
            return null;
        }
        try {
            return Long.parseLong(line.substring(0, dash));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the recipe keys of all selected entries from a JList that
     * contains lines like "201" or "201 ★".
     */
    private static List<String> getSelectedRecipeKeys(JList<String> list) {
        List<String> out = new ArrayList<>();
        int[] indices = list.getSelectedIndices();
        for (int idx : indices) {
            String line = list.getModel().getElementAt(idx);
            String key = line.split("\\s+")[0];
            out.add(key);
        }
        return out;
    }

    /**
     * Returns the recipe key of the selected item in a JList, or null.
     */
    private static String getSelectedRecipeKey(JList<String> list) {
        int idx = list.getSelectedIndex();
        if (idx < 0) {
            return null;
        }
        String line = list.getModel().getElementAt(idx);
        return line.split("\\s+")[0];
    }

    /**
     * React to changes in the CategoryViewModel state:
     *  - update the right-hand recipe list when filtered recipes change
     *  - show any info or error messages in dialog boxes
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        CategoryState state = viewModel.getState();

        // Update right-hand list from state
        categoryRecipeModel.clear();
        for (String s : state.getFilteredRecipes()) {
            categoryRecipeModel.addElement(s);
        }

        // Show messages (if any)
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Category Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else if (state.getInfoMessage() != null) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getInfoMessage(),
                    "Category Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
