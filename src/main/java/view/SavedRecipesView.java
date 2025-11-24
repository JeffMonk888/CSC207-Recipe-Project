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
        JButton backButton = new JButton("Refresh / Load");

        buttons.add(backButton);
        buttons.add(deleteButton);
        add(buttons, BorderLayout.SOUTH);

        // listener

        // 1. delete
        deleteButton.addActionListener(e -> {
            String selected = recipesList.getSelectedValue();
            if (selected != null) {
                // get id
                Long recipeId = extractId(selected);
                if (recipeId != null) {
                    controller.executeDelete(currentUserId, recipeId);
                    // refresh
                    controller.executeRetrieve(currentUserId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a recipe to delete.");
            }
        });

        // 2. retrieve
        backButton.addActionListener(e -> {
            controller.executeRetrieve(currentUserId);
        });

        // double click to get detail
        recipesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = recipesList.getSelectedValue();
                    if (selected != null) {
                        Long recipeId = extractId(selected);
                        if (recipeId != null) {
                            viewRecipeController.execute(recipeId);
                        }
                    }
                }
            }
        });
    }

    // get LOng id from str
    private Long extractId(String text) {
        try {
            int start = text.lastIndexOf("[ID:");
            int end = text.lastIndexOf("]");
            if (start != -1 && end != -1) {
                String idStr = text.substring(start + 4, end);
                return Long.parseLong(idStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
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
