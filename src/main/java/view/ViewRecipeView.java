package view;

import interfaceadapter.view_recipe.ViewRecipeState;
import interfaceadapter.view_recipe.ViewRecipeAbstractViewModel;
import interfaceadapter.saved_recipe.SaveRecipeController;
import interfaceadapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Swing view for the "View Recipe" screen.
 *
 * This panel observes the ViewRecipeViewModel and updates its labels and
 * text areas whenever the ViewModel's state changes.
 */
public class ViewRecipeView extends JPanel implements PropertyChangeListener {

    private final ViewRecipeAbstractViewModel viewModel;
    private final SaveRecipeController saveRecipeController;
    private final ViewManagerModel viewManagerModel;

    // UI components
    private final JLabel titleLabel;
    private final JLabel metaLabel;
    private final JLabel nutritionLabel;
    private final JLabel errorLabel;
    private final JLabel imageLabel;
    private final JButton saveButton;
    private final JButton backButton;
    private final JTextArea ingredientsArea;
    private final JTextArea stepsArea;

    public ViewRecipeView(ViewRecipeAbstractViewModel viewModel,
                          SaveRecipeController saveRecipeController,
                          ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.saveRecipeController = saveRecipeController;
        this.viewManagerModel = viewManagerModel;

        this.viewModel.addPropertyChangeListener(this);

        // Layout
        setPreferredSize(new Dimension(900, 600));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top: title + metadata + nutrition + Save button =====
        JPanel topPanel = new JPanel(new BorderLayout());

        // left/center text stack
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Recipe Details");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        metaLabel = new JLabel("");
        nutritionLabel = new JLabel("");

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(metaLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(nutritionLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(errorLabel);

        // right side: Save Recipe button
        saveButton = new JButton("Save Recipe");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);

        topPanel.add(textPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ===== Center: ingredients and instructions =====
        ingredientsArea = new JTextArea();
        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);

        stepsArea = new JTextArea();
        stepsArea.setEditable(false);
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);

        JScrollPane ingredientsScroll = new JScrollPane(ingredientsArea);
        ingredientsScroll.setBorder(BorderFactory.createTitledBorder("Ingredients"));

        JScrollPane stepsScroll = new JScrollPane(stepsArea);
        stepsScroll.setBorder(BorderFactory.createTitledBorder("Steps"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                ingredientsScroll,
                stepsScroll
        );
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);

        // ===== Bottom: image / source URL summary + Back button =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        imageLabel = new JLabel("");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(imageLabel, BorderLayout.CENTER);

        // NEW: back button on bottom-right
        backButton = new JButton("Back to Home");
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(backButton);
        bottomPanel.add(backPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // wire up Save Recipe button
        saveButton.addActionListener(e -> handleSaveRecipe());
        backButton.addActionListener(e -> viewManagerModel.setActiveViewName("home"));
        // Initial paint from current state (if any)
        updateFromState();
    }

    public String getViewName() {
        return ViewRecipeAbstractViewModel.VIEW_NAME;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            updateFromState();
        }
    }

    /**
     * Handle click on the "Save Recipe" button.
     * Uses the current ViewRecipeState's recipeId as the recipe key and
     * the current user id from the ViewManagerModel.
     */
    private void handleSaveRecipe() {
        ViewRecipeState state = viewModel.getState();
        if (state == null || state.getRecipeId() == null) {
            JOptionPane.showMessageDialog(this,
                    "No recipe is loaded to save.",
                    "Cannot Save",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long userId = viewManagerModel.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to save recipes.",
                    "Cannot Save",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // NEW: confirmation pop-up (single Save click → confirm → save)
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Save this recipe to your collection?",
                "Confirm Save",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (confirm != JOptionPane.OK_OPTION) {
            return; // user cancelled, do nothing
        }

        // Same key format: "a" + API recipe ID
        String recipeKey = "a" + state.getRecipeId();
        saveRecipeController.execute(userId, recipeKey);

        // Optional: visible feedback that matches your UC6 spec
        JOptionPane.showMessageDialog(
                this,
                "Recipe saved!",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Pulls data from the ViewModel's state and updates all UI components.
     */
    private void updateFromState() {
        ViewRecipeState state = viewModel.getState();
        if (state == null) {
            return;
        }

        // Error
        if (state.getErrorMessage() != null && !state.getErrorMessage().isBlank()) {
            errorLabel.setText(state.getErrorMessage());
        } else {
            errorLabel.setText(" ");
        }

        // Title
        if (state.getTitle() != null && !state.getTitle().isBlank()) {
            titleLabel.setText(state.getTitle());
        } else {
            titleLabel.setText("Recipe Details");
        }

        // Metadata: servings, time, source
        StringBuilder meta = new StringBuilder();
        if (state.getServings() != null) {
            meta.append("Servings: ").append(state.getServings()).append("    ");
        }
        if (state.getReadyInMinutes() != null) {
            meta.append("Ready in: ").append(state.getReadyInMinutes()).append(" min    ");
        }
        if (state.getSourceName() != null && !state.getSourceName().isBlank()) {
            meta.append("Source: ").append(state.getSourceName()).append("    ");
        }
        metaLabel.setText(meta.toString());

        // Nutrition
        StringBuilder nutrition = new StringBuilder();
        if (state.getCalories() != null && !state.getCalories().isBlank()) {
            nutrition.append("Calories: ").append(state.getCalories()).append("    ");
        }
        if (state.getProtein() != null && !state.getProtein().isBlank()) {
            nutrition.append("Protein: ").append(state.getProtein()).append("    ");
        }
        if (state.getFat() != null && !state.getFat().isBlank()) {
            nutrition.append("Fat: ").append(state.getFat()).append("    ");
        }
        if (state.getCarbohydrates() != null && !state.getCarbohydrates().isBlank()) {
            nutrition.append("Carbs: ").append(state.getCarbohydrates()).append("    ");
        }
        nutritionLabel.setText(nutrition.toString());

        // Ingredients
        List<String> ingredients = state.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String ing : ingredients) {
                sb.append("• ").append(ing).append("\n");
            }
            ingredientsArea.setText(sb.toString());
        } else {
            ingredientsArea.setText("");
        }

        // Steps
        List<String> steps = state.getSteps();
        if (steps != null && !steps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int stepNum = 1;
            for (String step : steps) {
                sb.append(stepNum++).append(") ").append(step).append("\n\n");
            }
            stepsArea.setText(sb.toString());
        } else {
            stepsArea.setText("");
        }

        // Image URL / source URL
        if (state.getImageUrl() != null && !state.getImageUrl().isBlank()) {
            imageLabel.setText("Image: " + state.getImageUrl());
        } else if (state.getSourceUrl() != null && !state.getSourceUrl().isBlank()) {
            imageLabel.setText("Source URL: " + state.getSourceUrl());
        } else {
            imageLabel.setText(" ");
        }
    }
}
