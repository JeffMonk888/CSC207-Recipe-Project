package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;

public class HomeView extends JPanel {

    private final ViewManagerModel viewManagerModel;

    public HomeView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setPreferredSize(new Dimension(600, 500));
        setLayout(new BorderLayout(10, 10));

        JPanel topBar = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Find My Recipe - Homepage", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton logoutButton = new JButton("Log out");
        Component leftSpacer = Box.createHorizontalStrut(logoutButton.getPreferredSize().width);

        topBar.add(leftSpacer, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        topBar.add(logoutButton, BorderLayout.EAST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 26f));
        welcomeLabel.setForeground(new Color(0, 150, 0));

        JLabel subtitleLabel = new JLabel("Find or create your next recipe");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 16f));

        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        JButton findNewRecipeButton = new JButton("Find new recipe");
        JButton makeNewRecipeButton = new JButton("Make new recipe");
        JButton favouritesButton = new JButton("Favourites ★");
        JButton fridgeButton = new JButton("My Fridge");

        Dimension buttonSize = new Dimension(260, 40);
        for (JButton button : new JButton[]{findNewRecipeButton, makeNewRecipeButton, favouritesButton, fridgeButton}) {
            button.setMaximumSize(buttonSize);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        centerPanel.add(findNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(makeNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(favouritesButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(fridgeButton);

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        logoutButton.addActionListener(e ->
                viewManagerModel.setActiveViewName("login")
        );

        findNewRecipeButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Find new recipe – to be implemented",
                        "Find new recipe",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        makeNewRecipeButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Make new recipe – to be implemented",
                        "Make new recipe",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        favouritesButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Favourites – to be implemented",
                        "Favourites",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        fridgeButton.addActionListener(e ->
                viewManagerModel.setActiveViewName("fridge")
        );
    }

    public String getViewName() {
        return "home";
    }
}
