package view;

import domain.entity.Recipe;
import interface_adapter.ViewManagerModel;
import interface_adapter.filter_recipes.FilterRecipesController;
import interface_adapter.filter_recipes.FilterRecipesState;
import interface_adapter.filter_recipes.FilterRecipesViewModel;
import usecase.filter_recipes.FilterRecipesInputData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class FilterRecipeView extends JPanel implements PropertyChangeListener {

    private final FilterRecipesViewModel viewModel;
    private final FilterRecipesController controller;
    private final ViewManagerModel viewManagerModel;

    // base recipes to filter – another screen (e.g. search results) will set this
    private List<Recipe> baseRecipes = new ArrayList<>();

    private final JTextField maxCaloriesField = new JTextField(10);
    private final JComboBox<FilterRecipesInputData.SortBy> sortByBox =
            new JComboBox<>(FilterRecipesInputData.SortBy.values());
    private final JComboBox<String> sortOrderBox =
            new JComboBox<>(new String[] { "Ascending", "Descending" });
    private final DefaultListModel<String> resultsModel = new DefaultListModel<>();
    private final JList<String> resultsList = new JList<>(resultsModel);

    public FilterRecipeView(FilterRecipesViewModel viewModel,
                            FilterRecipesController controller,
                            ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;

        this.viewModel.addPropertyChangeListener(this);
        sortOrderBox.setPrototypeDisplayValue("Descending");
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Filter & Sort Recipes", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        // Controls panel
        JPanel controls = new JPanel();
        controls.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        controls.add(new JLabel("Max calories (optional):"), gbc);
        gbc.gridx = 1;
        controls.add(maxCaloriesField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        controls.add(new JLabel("Sort by:"), gbc);
        gbc.gridx = 1;
        controls.add(sortByBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        controls.add(new JLabel("Sort order:"), gbc);
        gbc.gridx = 1;
        controls.add(sortOrderBox, gbc);
        row++;

        JButton applyButton = new JButton("Apply filters");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        controls.add(applyButton, gbc);

        add(controls, BorderLayout.NORTH);

        // Results list
        JScrollPane scrollPane = new JScrollPane(resultsList);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        bottom.add(backButton);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        applyButton.addActionListener(e -> applyFilters());
        backButton.addActionListener(e ->
                viewManagerModel.setActiveViewName("find-recipes"));
    }

    private void applyFilters() {
        // Parse max calories (optional)
        Double maxCalories = null;
        String text = maxCaloriesField.getText().trim();
        if (!text.isEmpty()) {
            try {
                maxCalories = Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                // If invalid, just ignore and treat as no calorie filter
            }
        }

        FilterRecipesInputData.SortBy sortBy =
                (FilterRecipesInputData.SortBy) sortByBox.getSelectedItem();
        String selectedOrder = (String) sortOrderBox.getSelectedItem();
        FilterRecipesInputData.SortOrder sortOrder =
                "Ascending".equals(selectedOrder)
                        ? FilterRecipesInputData.SortOrder.ASC
                        : FilterRecipesInputData.SortOrder.DESC;

        controller.execute(baseRecipes, maxCalories, sortBy, sortOrder);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt != null && !"state".equals(evt.getPropertyName())) {
            return;
        }

        FilterRecipesState state = viewModel.getState();
        resultsModel.clear();
        for (Recipe r : state.getResults()) {
            resultsModel.addElement(r.getTitle());
        }
    }

    /** Allow another screen (e.g. search results) to provide the recipes to filter. */
    public void setBaseRecipes(List<Recipe> recipes) {
        this.baseRecipes = recipes != null ? recipes : new ArrayList<>();
    }

    public String getViewName() {
        return FilterRecipesViewModel.VIEW_NAME;
    }
}
