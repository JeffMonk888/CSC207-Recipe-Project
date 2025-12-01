package view;

import interface_adapter.fridge.FridgeController;
import interface_adapter.fridge.FridgeViewModel;
import interface_adapter.fridge.FridgeState;
import interface_adapter.ViewManagerModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

// NEW: import the CSV-backed fridge DAO
import data.saved_ingredient.FileFridgeAccessObject;

/**
 * Swing view for the "What's in my fridge" feature.
 * Talks to FridgeController and observes FridgeViewModel.
 */
public class FridgeView extends JPanel implements ActionListener, PropertyChangeListener {

    private final FridgeController controller;
    private final FridgeViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    // UI components
    private final JTextField ingredientField = new JTextField(20);
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove Selected");
    private final JButton backButton = new JButton("Back to Home");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> ingredientList = new JList<>(listModel);

    private final JLabel errorLabel = new JLabel();

    public FridgeView(FridgeController controller,
                      FridgeViewModel viewModel,
                      ViewManagerModel viewManagerModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        // Register as listener to the ViewModel (for add/remove updates)
        this.viewModel.addPropertyChangeListener(this);

        // NEW: when this view becomes the active one, load items from fridge_items.csv
        this.viewManagerModel.addPropertyChangeListener(evt -> {
            if ("activeView".equals(evt.getPropertyName())) {
                String newView = (String) evt.getNewValue();
                if (FridgeViewModel.VIEW_NAME.equals(newView)) {
                    loadIngredientsFromStorageForCurrentUser();
                }
            }
        });

        setupUI();
        setupListeners();
    }

    // UI layout

    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel title = new JLabel("Ingredient in Fridge");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        titlePanel.add(title);

        // Center: list of ingredients
        ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(ingredientList);

        // Bottom: input + buttons + error label
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Ingredient:"));
        addPanel.add(ingredientField);
        addPanel.add(addButton);

        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removePanel.add(removeButton);

        errorLabel.setForeground(Color.RED);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(backButton);

        inputPanel.add(addPanel);
        inputPanel.add(removePanel);
        inputPanel.add(errorLabel);
        inputPanel.add(backPanel);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        addButton.setActionCommand("add");
        removeButton.setActionCommand("remove");
        backButton.setActionCommand("back");
        backButton.addActionListener(this);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
    }

    // Actions

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if ("add".equals(cmd)) {
            String text = ingredientField.getText().toLowerCase();
            Long userId = viewManagerModel.getCurrentUserId();
            controller.addIngredient(userId, text);

        } else if ("remove".equals(cmd)) {
            String selected = ingredientList.getSelectedValue();
            if (selected == null) {
                errorLabel.setText("Please select an ingredient to remove.");
            } else {
                Long userId = viewManagerModel.getCurrentUserId();
                controller.removeIngredient(userId, selected);
            }
        }
        else if ("back".equals(cmd)) {
            viewManagerModel.setActiveViewName("home");
        }
    }

    // NEW: load ingredients from fridge_items.csv for the active user,
    // and push them into the FridgeViewModel state.
    private void loadIngredientsFromStorageForCurrentUser() {
        Long userId = viewManagerModel.getCurrentUserId();
        if (userId == null) {
            // No logged-in user yet; nothing to load.
            return;
        }

        // Fresh DAO each time so we always read the latest contents of the file.
        FileFridgeAccessObject fridgeAccess = new FileFridgeAccessObject("fridge_items.csv");

        List<String> items = fridgeAccess.getItems(userId);

        FridgeState state = viewModel.getState();
        state.setIngredients(items);      // overwrite with what’s in the CSV for this user
        state.setErrorMessage(null);
        viewModel.fireStateChanged();     // triggers propertyChange(...) below
    }

    // ------------------ ViewModel updates ---------------------

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        FridgeState state = viewModel.getState();

        // Update text field
        ingredientField.setText(state.getCurrentIngredient());

        // Update list
        listModel.clear();
        List<String> ingredients = state.getIngredients();
        for (String item : ingredients) {
            listModel.addElement(item);
        }

        // Update error message
        String error = state.getErrorMessage();
        errorLabel.setText(error == null ? "" : error);
    }

    public String getViewName() {
        return FridgeViewModel.VIEW_NAME; // "fridge"
    }
}
