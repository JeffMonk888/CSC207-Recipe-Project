package app;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder builder = new AppBuilder();
            builder
                    .addLoginView()
                    .addSignUpView()
                    .addHomeView()
                    .show();
        });
    }

}
