package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.create_recipe.CreateRecipeAbstractViewModel;
import interfaceadapter.saved_recipe.SavedRecipeAbstractViewModel;

public class HomeView extends JPanel {

    private final ViewManagerModel viewManagerModel;

    private final JButton logoutButton = new JButton("Log out");
    private final JButton findNewRecipeButton = new JButton("Find new recipe");
    private final JButton makeNewRecipeButton = new JButton("Make new recipe");
    private final JButton savedRecipesButton = new JButton("Saved Recipes");
    private final JButton fridgeButton = new JButton("My Fridge");

    public HomeView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        initComponents();
        initListeners();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(600, 500));
        setLayout(new BorderLayout(10, 10));

        final JPanel topBar = createTopBar();
        final JPanel centerPanel = createCenterPanel();

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        final JPanel topBar = new JPanel(new BorderLayout());

        final JLabel title =
                new JLabel("Find My Recipe - Homepage", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        final Component leftSpacer =
                Box.createHorizontalStrut(logoutButton.getPreferredSize().width);

        topBar.add(leftSpacer, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        topBar.add(logoutButton, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createCenterPanel() {
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 60, 20, 60));

        final JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(
                welcomeLabel.getFont().deriveFont(Font.BOLD, 26f));
        welcomeLabel.setForeground(new Color(0, 150, 0));

        final JLabel subtitleLabel =
                new JLabel("Find or create your next recipe");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(
                subtitleLabel.getFont().deriveFont(Font.PLAIN, 16f));

        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        final Dimension buttonSize = new Dimension(260, 40);
        final JButton[] buttons = {findNewRecipeButton, makeNewRecipeButton, savedRecipesButton, fridgeButton
        };
        for (JButton button : buttons) {
            button.setMaximumSize(buttonSize);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        centerPanel.add(findNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(makeNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(savedRecipesButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(fridgeButton);

        return centerPanel;
    }

    private void initListeners() {
        makeNewRecipeButton.addActionListener(event -> {
            viewManagerModel.setActiveViewName(CreateRecipeAbstractViewModel.VIEW_NAME);
        });

        logoutButton.addActionListener(event -> {
            viewManagerModel.setActiveViewName("login");
        });

        findNewRecipeButton.addActionListener(event -> {
            viewManagerModel.setActiveViewName("find-recipes");
        });

        savedRecipesButton.addActionListener(event -> {
            viewManagerModel.setActiveViewName(SavedRecipeAbstractViewModel.VIEW_NAME);
        });

        fridgeButton.addActionListener(event -> {
            viewManagerModel.setActiveViewName("fridge");
        });
    }

    public String getViewName() {
        return "home";
    }
}
