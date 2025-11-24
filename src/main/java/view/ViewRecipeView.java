package view;

import interface_adapter.view_recipe.ViewRecipeState;
import interface_adapter.view_recipe.ViewRecipeViewModel;

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

    private final ViewRecipeViewModel viewModel;

    // UI components
    private final JLabel titleLabel;
    private final JLabel metaLabel;
    private final JLabel nutritionLabel;
    private final JLabel errorLabel;
    private final JLabel imageLabel;

    private final JTextArea ingredientsArea;
    private final JTextArea stepsArea;

    public ViewRecipeView(ViewRecipeViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // Layout
        setPreferredSize(new Dimension(900, 600));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: title + metadata + nutrition
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Recipe Details");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        metaLabel = new JLabel("");
        nutritionLabel = new JLabel("");

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(metaLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(nutritionLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(errorLabel);

        add(topPanel, BorderLayout.NORTH);

        // ===== Center: ingredients and instruction to made =====
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

        // ===== Bottom: image / source URL summary =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        imageLabel = new JLabel("");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(imageLabel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Initial paint from current state (if any)
        updateFromState();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            updateFromState();
        }
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
            meta.append("Source: ").append(state.getSourceName());
        }
        metaLabel.setText(meta.toString());

        // Nutrition line
        StringBuilder nutr = new StringBuilder();
        if (state.getCalories() != null && !state.getCalories().isBlank()) {
            nutr.append("Calories: ").append(state.getCalories()).append("    ");
        }
        if (state.getProtein() != null && !state.getProtein().isBlank()) {
            nutr.append("Protein: ").append(state.getProtein()).append("    ");
        }
        if (state.getFat() != null && !state.getFat().isBlank()) {
            nutr.append("Fat: ").append(state.getFat()).append("    ");
        }
        if (state.getCarbohydrates() != null && !state.getCarbohydrates().isBlank()) {
            nutr.append("Carbs: ").append(state.getCarbohydrates());
        }
        nutritionLabel.setText(nutr.toString());

        // Ingredients
        List<String> ingredients = state.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String line : ingredients) {
                sb.append("• ").append(line).append("\n");
            }
            ingredientsArea.setText(sb.toString());
        } else {
            ingredientsArea.setText("");
        }

        // Instructions
        List<String> steps = state.getSteps();
        if (steps != null && !steps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String step : steps) {
                sb.append(step).append("\n\n");
            }
            stepsArea.setText(sb.toString());
        } else {
            stepsArea.setText("");
        }

        // Image URL
        if (state.getImageUrl() != null && !state.getImageUrl().isBlank()) {
            imageLabel.setText("Image: " + state.getImageUrl());
        } else if (state.getSourceUrl() != null && !state.getSourceUrl().isBlank()) {
            imageLabel.setText("Source URL: " + state.getSourceUrl());
        } else {
            imageLabel.setText(" ");
        }
    }
}
