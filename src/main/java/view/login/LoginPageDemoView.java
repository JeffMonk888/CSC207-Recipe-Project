package view.login;

import javax.swing.*;

public class LoginPageDemoView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Recipe App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // our login panel
            frame.setContentPane(new LoginPage());

            frame.pack();                 // size window to fit components
            frame.setLocationRelativeTo(null); // center on screen
            frame.setVisible(true);
        });
    }
}
