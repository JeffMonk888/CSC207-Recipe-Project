package view.login;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel errorLabel;

    public LoginPage() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Recipe App - Login");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Log in");
        errorLabel = new JLabel(" ");      // blank for now
        errorLabel.setForeground(Color.RED);

        // Row 0 – title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Row 1 – username label
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(usernameLabel, gbc);

        // Row 1 – username field
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Row 2 – password label
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(passwordLabel, gbc);

        // Row 2 – password field
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Row 3 – login button
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        // Row 4 – error label
        gbc.gridy = 4;
        add(errorLabel, gbc);

        loginButton.addActionListener(e -> handleLoginClick());
    }
    private void handleLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setError("Please enter both username and password");
            return;
        }

        if (username.equals("test") && password.equals("1234")) {
            setError(" ");
            JOptionPane.showMessageDialog(
                    this,
                    "Login successful (temporary check)",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            setError("Invalid username or password");
        }
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}
