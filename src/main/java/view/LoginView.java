package view;

import interface_adapter.ViewManagerModel;
import usecase.auth.SignUpAuth;
import domain.entity.User;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    private final ViewManagerModel viewManagerModel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;
    private final JLabel errorLabel;

    public LoginView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setPreferredSize(new Dimension(450, 280));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Recipe Manager");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        signupButton = new JButton("Sign up");
        loginButton = new JButton("Log in");
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
        add(signupButton, gbc);
        gbc.gridx = 1;
        add(loginButton, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(errorLabel, gbc);

        loginButton.addActionListener(e -> handleLoginClick());
        signupButton.addActionListener(e -> openSignupView());
    }

    public String getViewName() {
        return "login";
    }

    private void handleLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        User user = SignUpAuth.authenticateAndGetUser(username, password);
        if (username.isEmpty() || password.isEmpty()) {
            setError("Please enter both username and password");
            return;
        }

        if (SignUpAuth.authenticate(username, password)) {
            setError(" ");
            viewManagerModel.setActiveViewName("home");
            viewManagerModel.setCurrentUserId(user.getId());
        } else {
            setError("Invalid username or password");
        }
    }

    private void openSignupView() {
        viewManagerModel.setActiveViewName("signup");
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

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getSignupButton() {
        return signupButton;
    }
}
