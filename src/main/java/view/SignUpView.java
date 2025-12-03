package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import interfaceadapter.ViewManagerModel;
import usecase.auth.SignUpAuth;

public class SignUpView extends JPanel {

    private final ViewManagerModel viewManagerModel;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JLabel errorLabel;
    private final JButton backButton;
    private final JButton createButton;

    public SignUpView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        setLayout(new GridBagLayout());

        final GridBagConstraints constraints = createDefaultConstraints();

        final JLabel title = createTitleLabel();
        final JLabel usernameLabel = new JLabel("Username:");
        final JLabel passwordLabel = new JLabel("Password:");
        final JLabel confirmLabel = new JLabel("Confirm password:");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        confirmField = new JPasswordField(18);
        createButton = new JButton("Create account");
        backButton = new JButton("Back to login");
        errorLabel = createErrorLabel();

        layoutComponents(constraints, title, usernameLabel, passwordLabel, confirmLabel);
        attachListeners();
    }

    private GridBagConstraints createDefaultConstraints() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    private JLabel createTitleLabel() {
        final JLabel title = new JLabel("Create Account");
        final Font currentFont = title.getFont();
        title.setFont(currentFont.deriveFont(Font.BOLD, 22f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        return title;
    }

    private JLabel createErrorLabel() {
        final JLabel error = new JLabel(" ");
        error.setForeground(Color.RED);
        error.setHorizontalAlignment(SwingConstants.CENTER);
        return error;
    }

    private void layoutComponents(
            GridBagConstraints constraints,
            JLabel title,
            JLabel usernameLabel,
            JLabel passwordLabel,
            JLabel confirmLabel
    ) {
        layoutTitleRow(constraints, title);
        layoutUsernameRow(constraints, usernameLabel);
        layoutPasswordRow(constraints, passwordLabel);
        layoutConfirmRow(constraints, confirmLabel);
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

    private void layoutConfirmRow(GridBagConstraints constraints, JLabel confirmLabel) {
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.weightx = 0.0;
        add(confirmLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        add(confirmField, constraints);
    }

    private void layoutButtonsRow(GridBagConstraints constraints) {
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        add(createButton, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.5;
        add(backButton, constraints);
    }

    private void layoutErrorRow(GridBagConstraints constraints) {
        constraints.gridy = 5;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        add(errorLabel, constraints);
    }

    private void attachListeners() {
        createButton.addActionListener(execute -> handleCreateClick());
        backButton.addActionListener(
                execute -> viewManagerModel.setActiveViewName("login")
        );
    }

    public String getViewName() {
        return "signup";
    }

    private void handleCreateClick() {
        final String username = usernameField.getText().trim();
        final String password = new String(passwordField.getPassword());
        final String confirm = new String(confirmField.getPassword());

        String errorMessage = null;

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            errorMessage = "Please fill in all fields";
        }
        else if (!password.equals(confirm)) {
            errorMessage = "Passwords do not match";
        }
        else {
            final boolean ok = SignUpAuth.register(username, password);
            if (!ok) {
                errorMessage = "Username already exists";
            }
        }

        if (errorMessage != null) {
            setError(errorMessage);
        }
        else {
            setError(" ");
            JOptionPane.showMessageDialog(
                    this,
                    "Account created! You can now log in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            viewManagerModel.setActiveViewName("login");
        }
    }

    private void setError(String message) {
        errorLabel.setText(message);
    }
}
