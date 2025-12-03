package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.create_recipe.CreateRecipeController;
import interface_adapter.create_recipe.CreateRecipeState;
import interface_adapter.create_recipe.CreateRecipeViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CreateRecipeView extends JPanel implements PropertyChangeListener {
    public final String viewName = "create recipe";

    private final CreateRecipeViewModel viewModel;
    private final CreateRecipeController controller;
    private final ViewManagerModel viewManagerModel;


    // UI Components
    private final JTextField titleField = new JTextField(20);
    private final JTextArea ingredientsArea = new JTextArea(3, 25);
    private final JTextArea instructionsArea = new JTextArea(5, 25);
    private final JButton createButton = new JButton("Create Recipe");
    private final JButton backButton = new JButton("Back to Home");

    public CreateRecipeView(CreateRecipeViewModel viewModel,
                            CreateRecipeController controller,
                            ViewManagerModel viewManagerModel
                            ) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewManagerModel = viewManagerModel;


        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Create New Recipe");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        Dimension titlePref = titleField.getPreferredSize();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePref.height));
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(new JLabel("Ingredients (comma separated):"));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScroll = new JScrollPane(ingredientsArea);
// keep this scroll pane from stretching too tall
        Dimension ingPref = ingredientsScroll.getPreferredSize();
        ingredientsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, ingPref.height));
        formPanel.add(ingredientsScroll);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(new JLabel("Instructions (one step per line):"));
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
// same idea for instructions
        Dimension instrPref = instructionsScroll.getPreferredSize();
        instructionsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, instrPref.height));
        formPanel.add(instructionsScroll);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(createButton);
        add(buttonPanel, BorderLayout.SOUTH);


        createButton.addActionListener(e -> {
            String title = titleField.getText();
            String ingredients = ingredientsArea.getText();
            String instructions = instructionsArea.getText();

            if (title == null || title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to create and save this recipe?\nTitle: " + title,
                    "Confirm Save",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                Long userId = viewManagerModel.getCurrentUserId();
                controller.execute(userId, title, ingredients, instructions);
            }
        });

        backButton.addActionListener(e -> {
            viewManagerModel.setActiveViewName("home");
        });
    }
    public String getViewName() {
        return viewModel.getViewName();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CreateRecipeState state = viewModel.getState();
        if (state.getMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getMessage());
            state.setMessage(null);
        }
    }
}
