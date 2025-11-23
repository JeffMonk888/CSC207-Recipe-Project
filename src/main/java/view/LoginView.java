package view;

import usecase.auth.SignUpAuth;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    private final JFrame parentFrame;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;
    private final JLabel errorLabel;


    public LoginView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
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
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(signupButton, gbc);
        gbc.gridx = 1;
        add(loginButton, gbc);
        // Row 4 – error label
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(errorLabel, gbc);

        loginButton.addActionListener(e -> handleLoginClick());
        signupButton.addActionListener(e -> openSignupWindow());
    }

    private void handleLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setError("Please enter both username and password");
            return;
        }
        if (SignUpAuth.authenticate(username, password)) {
            setError(" ");
            // open HomePage instead of just a popup
            parentFrame.setContentPane(new HomeView(username, parentFrame));
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
        } else {
            setError("Invalid username or password");
        }
    }
    private void openSignupWindow() {
        JFrame signupFrame = new JFrame("Sign up - Recipe Manager");
        signupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        signupFrame.setContentPane(new SignUpView());
        signupFrame.pack();
        signupFrame.setLocationRelativeTo(this); // center near login
        signupFrame.setVisible(true);
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
