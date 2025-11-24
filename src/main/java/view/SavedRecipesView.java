package view;

import interface_adapter.saved_recipe.SavedRecipeController;
import interface_adapter.saved_recipe.SavedRecipeState;
import interface_adapter.saved_recipe.SavedRecipeViewModel;
import interface_adapter.view_recipe.ViewRecipeController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SavedRecipesView extends JPanel implements PropertyChangeListener {

    public final String viewName = "saved recipes"; // for CardLayout

    private final Long currentUserId;

    private final JList<String> recipesList;
    private final DefaultListModel<String> listModel;

    public SavedRecipesView(SavedRecipeViewModel viewModel,
                            SavedRecipeController controller,
                            ViewRecipeController viewRecipeController,
                            Long userId) {
        this.currentUserId = userId;

        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Saved");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // list of recipes
        listModel = new DefaultListModel<>();
        recipesList = new JList<>(listModel);
        add(new JScrollPane(recipesList), BorderLayout.CENTER);

        // bottom
        JPanel buttons = new JPanel();
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh / Load");

        buttons.add(refreshButton);
        buttons.add(deleteButton);
        add(buttons, BorderLayout.SOUTH);

        // 1. delete
        deleteButton.addActionListener(e -> {
            String selected = recipesList.getSelectedValue();
            if (selected != null) {
                String recipeKey = extractRecipeKey(selected);
                if (recipeKey != null) {
                    controller.executeDelete(currentUserId, recipeKey);
                    // refresh
                    controller.executeRetrieve(currentUserId);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not find recipe key in selected item.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a recipe to delete.");
            }
        });

        // 2. retrieve / refresh
        refreshButton.addActionListener(e -> controller.executeRetrieve(currentUserId));

        // 3. double click to view detail
        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = recipesList.getSelectedValue();
                    if (selected != null) {
                        String recipeKey = extractRecipeKey(selected);

                        if (recipeKey != null) {
                            viewRecipeController.execute(recipeKey);
                        } else {
                            JOptionPane.showMessageDialog(SavedRecipesView.this,
                                    "Could not find recipe key in selected item.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    // Extract String recipeKey from display text, e.g. "Pasta [KEY:a716429]"
    private String extractRecipeKey(String text) {
        int start = text.lastIndexOf("[KEY:");
        int end = text.lastIndexOf("]");
        if (start != -1 && end != -1 && end > start + 5) {
            // "[KEY:" is 5 characters
            String key = text.substring(start + 5, end);
            return key.trim();  // e.g. "a716429" or "c3"
        }
        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SavedRecipeState state = (SavedRecipeState) evt.getNewValue();
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage());
        }

        listModel.clear();
        for (String recipeStr : state.getSavedRecipes()) {
            listModel.addElement(recipeStr);
        }
    }
}
