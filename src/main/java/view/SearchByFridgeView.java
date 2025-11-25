package view;

import domain.entity.RecipePreview;
import interface_adapter.search_by_fridge.SearchByFridgeController;
import interface_adapter.search_by_fridge.SearchByFridgeState;
import interface_adapter.search_by_fridge.SearchByFridgeViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Swing view for "Search recipes using my fridge ingredients".
 * Talks to SearchByFridgeController and observes SearchByFridgeViewModel.
 */
public class SearchByFridgeView extends JPanel
        implements ActionListener, PropertyChangeListener {

    public interface RecipeSelectionListener {
        void onRecipeSelected(String recipeKey);
    }

    private static final int PAGE_SIZE = 10;

    private final SearchByFridgeController controller;
    private final SearchByFridgeViewModel viewModel;
    private final Long userId;
    private final RecipeSelectionListener recipeSelectionListener; // NEW

    // UI components
    private final JButton searchButton = new JButton("Search with my fridge");
    private final JButton loadMoreButton = new JButton("Load more");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipesList = new JList<>(listModel);
    private final JLabel errorLabel = new JLabel();
    private final JLabel infoLabel = new JLabel();

    public SearchByFridgeView(SearchByFridgeController controller,
                              SearchByFridgeViewModel viewModel,
                              Long userId,
                              RecipeSelectionListener recipeSelectionListener) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.userId = userId;
        this.recipeSelectionListener = recipeSelectionListener;

        this.viewModel.addPropertyChangeListener(this);

        setupUI();
        setupListeners();

        // Render initial state once
        propertyChange(null);
    }

    // UI layout

    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel title = new JLabel("Search Recipes Using My Fridge");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(title);

        // Top controls (search + load more)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(searchButton);
        controlPanel.add(loadMoreButton);

        // Center: recipe list
        recipesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipesList);

        // Bottom: info + error label
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        infoLabel.setForeground(Color.DARK_GRAY);
        errorLabel.setForeground(Color.RED);

        bottomPanel.add(infoLabel);
        bottomPanel.add(errorLabel);

        add(titlePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        searchButton.setActionCommand("search");
        loadMoreButton.setActionCommand("loadMore");

        searchButton.addActionListener(this);
        loadMoreButton.addActionListener(this);

        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recipeSelectionListener != null) {
                    int index = recipesList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        List<RecipePreview> recipes = viewModel.getState().getRecipes();
                        if (index < recipes.size()) {
                            RecipePreview selected = recipes.get(index);
                            recipeSelectionListener.onRecipeSelected(selected.recipeKey);
                        }
                    }
                }
            }
        });
    }

    // Actions

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if ("search".equals(cmd)) {
            // Start from the beginning: offset = 0
            controller.search(userId, PAGE_SIZE, 0);

        } else if ("loadMore".equals(cmd)) {
            SearchByFridgeState state = viewModel.getState();
            if (state.hasMore()) {
                controller.search(userId, PAGE_SIZE, state.getOffset());
            } else {
                errorLabel.setText("No more recipes to load.");
            }
        }
    }

    // ViewModel updates

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SearchByFridgeState state = viewModel.getState();

        // Update recipe list
        listModel.clear();
        List<RecipePreview> recipes = state.getRecipes();
        for (RecipePreview r : recipes) {
            String display = r.title;
            if (r.likes != 0) {
                display += "  (" + r.likes + " likes)";
            }
            listModel.addElement(display);
        }

        // Update info + error messages
        String error = state.getErrorMessage();
        errorLabel.setText(error == null ? "" : error);

        String infoText = "Loaded " + recipes.size() + " recipes";
        if (state.hasMore()) {
            infoText += " — more available";
        } else {
            infoText += " — no more recipes";
        }
        infoLabel.setText(infoText);

        // Enable/disable loadMore button
        loadMoreButton.setEnabled(state.hasMore());
    }
}
