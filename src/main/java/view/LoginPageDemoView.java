package view;

import javax.swing.*;

public class LoginPageDemoView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Recipe Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // our login panel
            frame.setContentPane(new LoginPage(frame));
            frame.pack();                 // size window to fit components
            frame.setLocationRelativeTo(null); // center on screen
            frame.setVisible(true);
        });
    }
}
