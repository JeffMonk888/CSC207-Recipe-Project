package view;

import interface_adapter.ViewManagerModel;
import usecase.auth.SignUpAuth;

import javax.swing.*;
import java.awt.*;

public class SignUpView extends JPanel {

    private final ViewManagerModel viewManagerModel;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JButton createButton;
    private final JLabel errorLabel;

    public SignUpView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setPreferredSize(new Dimension(450, 320));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmLabel = new JLabel("Confirm password:");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        confirmField = new JPasswordField(18);
        createButton = new JButton("Create account");
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(usernameLabel, gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        add(passwordLabel, gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        add(confirmLabel, gbc);
        gbc.gridx = 1;
        add(confirmField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(createButton, gbc);

        gbc.gridy = 5;
        add(errorLabel, gbc);

        createButton.addActionListener(e -> handleCreateClick());
    }

    public String getViewName() {
        return "signup";
    }

    private void handleCreateClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

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

        setError(" ");

        JOptionPane.showMessageDialog(
                this,
                "Account created! You can now log in.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        viewManagerModel.setActiveViewName("login");
    }

    private void setError(String message) {
        errorLabel.setText(message);
    }
}
