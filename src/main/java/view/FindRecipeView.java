package view;

import interfaceadapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;

public class FindRecipeView extends JPanel {

    private final ViewManagerModel viewManagerModel;

    public FindRecipeView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setPreferredSize(new Dimension(600, 500));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Top title
        JLabel title = new JLabel("Find New Recipes", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, BorderLayout.NORTH);

        // Center buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel subtitle = new JLabel("How would you like to search?");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(subtitle);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton searchByIngredientsButton = new JButton("Search by Ingredients");
        JButton whatCanIMakeButton = new JButton("Makeable Recipes");

        Dimension buttonSize = new Dimension(280, 40);
        for (JButton b : new JButton[]{searchByIngredientsButton, whatCanIMakeButton}) {
            b.setMaximumSize(buttonSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(b);
            centerPanel.add(Box.createVerticalStrut(10));
        }
        add(centerPanel, BorderLayout.CENTER);

        // Bottom: Back to Home
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back to Home");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions (for now: placeholders for UC2/3/4)
        searchByIngredientsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "UC2: Search by Ingredients – to be implemented",
                        "Search by Ingredients",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        whatCanIMakeButton.addActionListener(e ->
                viewManagerModel.setActiveViewName("search-by-fridge")
        );


        backButton.addActionListener(e ->
                viewManagerModel.setActiveViewName("home")
        );
    }

    public String getViewName() {
        return "find-recipes";
    }
}
