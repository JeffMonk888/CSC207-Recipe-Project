package view;

import javax.swing.*;
import java.awt.*;

public class HomeView extends JPanel {

    public HomeView(String username, JFrame frame) {
        // Overall size
        setPreferredSize(new Dimension(600, 500));
        setLayout(new BorderLayout(10, 10));

        // ----- Top bar: title + (optional) logout -----
        JPanel topBar = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Find My Recipe - Homepage", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton logoutButton = new JButton("Log out");

// 🔹 spacer on the left same size as logout button
        Component leftSpacer = Box.createHorizontalStrut(logoutButton.getPreferredSize().width);

        topBar.add(leftSpacer, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        topBar.add(logoutButton, BorderLayout.EAST);

        // ----- Center: welcome + buttons -----
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 26f));
        welcomeLabel.setForeground(new Color(0, 150, 0)); // light green-ish

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.PLAIN, 18f));

        // Spacing helper
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(usernameLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        // Buttons styled to look like your sketch
        JButton findNewRecipeButton = new JButton("Find new recipe");
        JButton makeNewRecipeButton = new JButton("Make new recipe");
        JButton favouritesButton = new JButton("Favourites ★");

        Dimension buttonSize = new Dimension(260, 40);
        for (JButton button : new JButton[]{findNewRecipeButton, makeNewRecipeButton, favouritesButton}) {
            button.setMaximumSize(buttonSize);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        centerPanel.add(findNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(makeNewRecipeButton);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(favouritesButton);

        // ----- Add panels to main layout -----
        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // ----- Button behaviour (temporary) -----

        logoutButton.addActionListener(e -> {
            frame.setContentPane(new LoginView(frame));
            frame.pack();
            frame.setLocationRelativeTo(null);
        });

        findNewRecipeButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Find new recipe – to be implemented",
                        "Find new recipe",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        makeNewRecipeButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Make new recipe – to be implemented",
                        "Make new recipe",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        favouritesButton.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Favourites – to be implemented",
                        "Favourites",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
    }
}
