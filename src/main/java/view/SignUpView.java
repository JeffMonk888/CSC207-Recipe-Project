package view;

import usecase.auth.SignUpAuth;

import javax.swing.*;
import java.awt.*;

public class SignUpView extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JButton createButton;
    private final JLabel errorLabel;

    public SignUpView() {
        setPreferredSize(new Dimension(450, 260));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("username:");
        JLabel passwordLabel = new JLabel("password:");
        JLabel confirmLabel = new JLabel("confirm password:");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        confirmField = new JPasswordField(18);

        createButton = new JButton("Create account");
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // --- Layout ---
        // Row 0 – title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Row 1 – username
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        // Row 2 – password
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        // Row 3 – confirm password
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(confirmLabel, gbc);

        gbc.gridx = 1;
        add(confirmField, gbc);

        // Row 4 – button
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(createButton, gbc);

        // Row 5 – error
        gbc.gridy = 5;
        add(errorLabel, gbc);

        // behaviour
        createButton.addActionListener(e -> handleCreateAccount());
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setError("Please fill in all fields");
            return;
        }

        if (!password.equals(confirm)) {
            setError("Passwords do not match");
            return;
        }

        boolean ok = SignUpAuth.register(username, password);
        if (!ok) {
            setError("Username already exists");
            return;
        }

        // success!
        setError(" ");
        JOptionPane.showMessageDialog(
                this,
                "Account created! You can now log in.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Close the window this panel is in
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void setError(String message) {
        errorLabel.setText(message);
    }
}
