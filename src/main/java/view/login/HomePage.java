package view.login;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    public HomePage(String username, JFrame frame) {
        setPreferredSize(new Dimension(600, 400));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Recipe Manager - Home", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel welcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcome.setFont(welcome.getFont().deriveFont(Font.PLAIN, 18f));

        JButton logoutButton = new JButton("Log out");

        // layout
        JPanel center = new JPanel(new BorderLayout());
        center.add(welcome, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

        // when user logs out, go back to LoginPage
        logoutButton.addActionListener(e -> {
            frame.setContentPane(new LoginPage(frame));
            frame.pack();
            frame.setLocationRelativeTo(null);
        });
    }
}
