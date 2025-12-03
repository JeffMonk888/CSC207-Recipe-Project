package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import domain.entity.User;
import interfaceadapter.ViewManagerModel;
import usecase.auth.SignUpAuth;

public class LoginView extends JPanel {

    private final ViewManagerModel viewManagerModel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton signupButton;
    private final JLabel errorLabel;

    public LoginView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setLayout(new GridBagLayout());

        final GridBagConstraints constraints = createDefaultConstraints();
        final JLabel title = createTitleLabel();
        final JLabel usernameLabel = new JLabel("Username:");
        final JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        loginButton = new JButton("Log in");
        signupButton = new JButton("Create account");
        errorLabel = createErrorLabel();

        layoutComponents(constraints, title, usernameLabel, passwordLabel);
        attachListeners();
    }

    private GridBagConstraints createDefaultConstraints() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    private JLabel createTitleLabel() {
        final JLabel title = new JLabel("Welcome back");
        final Font base = title.getFont();
        title.setFont(base.deriveFont(Font.BOLD, 22f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        return title;
    }

    private JLabel createErrorLabel() {
        final JLabel label = new JLabel(" ");
        label.setForeground(Color.RED);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void layoutComponents(
            GridBagConstraints constraints,
            JLabel title,
            JLabel usernameLabel,
            JLabel passwordLabel
    ) {
        layoutTitleRow(constraints, title);
        layoutUsernameRow(constraints, usernameLabel);
        layoutPasswordRow(constraints, passwordLabel);
        layoutButtonsRow(constraints);
        layoutErrorRow(constraints);
    }

    private void layoutTitleRow(GridBagConstraints constraints, JLabel title) {
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        add(title, constraints);
    }

    private void layoutUsernameRow(GridBagConstraints constraints, JLabel usernameLabel) {
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        add(usernameLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        add(usernameField, constraints);
    }

    private void layoutPasswordRow(GridBagConstraints constraints, JLabel passwordLabel) {
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.weightx = 0.0;
        add(passwordLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        add(passwordField, constraints);
    }

    private void layoutButtonsRow(GridBagConstraints constraints) {
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        add(loginButton, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.5;
        add(signupButton, constraints);
    }

    private void layoutErrorRow(GridBagConstraints constraints) {
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        add(errorLabel, constraints);
    }

    private void attachListeners() {
        loginButton.addActionListener(execute -> handleLoginClick());
        signupButton.addActionListener(
                execute -> viewManagerModel.setActiveViewName("signup")
        );
    }

    public String getViewName() {
        return "login";
    }

    private void handleLoginClick() {
        final String username = usernameField.getText().trim();
        final String password = new String(passwordField.getPassword());

        String errorMessage = null;
        User user = null;

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "Please enter both username and password";
        }
        else {
            user = SignUpAuth.authenticateAndGetUser(username, password);
            if (user == null) {
                errorMessage = "Invalid username or password";
            }
        }

        if (errorMessage != null) {
            setError(errorMessage);
        }
        else {
            setError(" ");
            viewManagerModel.setActiveViewName("home");
            viewManagerModel.setCurrentUserId(user.getId());
        }
    }

    private void openSignupView() {
        viewManagerModel.setActiveViewName("signup");
    }

    /**
     * Updates the error label with the given message.
     *
     * @param message text to display in the error label
     */
    public void setError(String message) {
        errorLabel.setText(message);
    }

}
