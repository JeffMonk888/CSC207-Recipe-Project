package view;

import data.rating.InMemoryUserRatingGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipeState;
import interface_adapter.rate_recipe.RateRecipeViewModel;
import interface_adapter.rate_recipe.RateRecipePresenter;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.UserRatingDataAccessInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * GUI view for UC9: Rate Recipe.
 *
 * This version uses the Rate Recipe interface adapter:
 *  - RateRecipeViewModel / RateRecipeState
 *  - RateRecipeController
 *  - RateRecipePresenter as the OutputBoundary
 *
 * The view still reads saved recipes from UserSavedRecipeAccessObject
 * to populate the list on the left.
 */
public class RateRecipeView extends JFrame implements PropertyChangeListener {

    private static final long USER_ID = 1L;

    // Gateways
    private final UserSavedRecipeAccessObject savedGateway;
    private final UserRatingDataAccessInterface ratingGateway;

    // Interface adapters
    private final RateRecipeViewModel viewModel;
    private final RateRecipeController controller;

    // Swing components
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> recipeList = new JList<>(listModel);
    private final JSpinner spinner;
    private final JLabel messageLabel = new JLabel(" ");

    /**
     * Construct a RateRecipeView wired with in-memory gateways and the
     * interface adapter objects (for demo use).
     */
    public RateRecipeView() {
        super("Rate Recipe");

        // ===== Gateways =====
        this.savedGateway = new UserSavedRecipeAccessObject("user_recipe_links.csv");
        this.ratingGateway = new InMemoryUserRatingGateway();

        seedDemoData(savedGateway);

        // ===== ViewModel & Presenter =====
        this.viewModel = new RateRecipeViewModel();
        RateRecipePresenter presenter = new RateRecipePresenter(viewModel);

        // ===== Interactor & Controller =====
        RateRecipeInputBoundary interactor =
                new RateRecipeInteractor(ratingGateway, presenter);
        this.controller = new RateRecipeController(interactor);

        // Listen to ViewModel updates
        this.viewModel.addPropertyChangeListener(this);

        // ===== Build UI =====
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // Left: saved recipes
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Saved Recipes (user " + USER_ID + ")"));
        scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.setPreferredSize(new Dimension(260, 360));
        root.add(scrollPane, BorderLayout.WEST);

        // Right: rating controls
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 20, 10, 10));

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        ratingPanel.setBorder(BorderFactory.createTitledBorder("Rating"));
        ratingPanel.setPreferredSize(new Dimension(260, 200));

        // Spinner for stars: 0.0..5.0 step 0.5
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, 5.0, 0.5);
        spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(80, 32));

        // Enlarge font inside the spinner editor a bit
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setColumns(3);
            tf.setFont(tf.getFont().deriveFont(Font.BOLD, 18f));
        }

        JLabel starLabel = new JLabel("★");
        starLabel.setFont(starLabel.getFont().deriveFont(Font.BOLD, 22f));

        JButton rateButton = new JButton("Save Rating");
        JButton clearButton = new JButton("Clear Rating");

        ratingPanel.add(new JLabel("Stars:"));
        ratingPanel.add(spinner);
        ratingPanel.add(starLabel);
        ratingPanel.add(rateButton);
        ratingPanel.add(clearButton);

        // Message label
        messageLabel.setForeground(new Color(0, 80, 0));
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.add(messageLabel);

        right.add(ratingPanel);
        right.add(Box.createVerticalStrut(10));
        right.add(messagePanel);

        root.add(right, BorderLayout.CENTER);

        // ===== Button actions =====

        rateButton.addActionListener(e -> {
            String recipeId = getSelectedRecipeKey();
            if (recipeId == null) {
                JOptionPane.showMessageDialog(
                        RateRecipeView.this,
                        "Please select a recipe on the left.",
                        "Rate Recipe",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            double stars = (Double) spinner.getValue();
            controller.rate(USER_ID, recipeId, stars);
            refreshList();
        });

        clearButton.addActionListener(e -> {
            String recipeId = getSelectedRecipeKey();
            if (recipeId == null) {
                JOptionPane.showMessageDialog(
                        RateRecipeView.this,
                        "Please select a recipe on the left.",
                        "Clear Rating",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            controller.clear(USER_ID, recipeId);
            refreshList();
        });

        // Initial list load
        refreshList();
    }

    /**
     * Reloads the left-hand list with saved recipes and their ratings.
     */
    private void refreshList() {
        listModel.clear();

        for (SavedRecipe sr : savedGateway.findByUserId(USER_ID)) {
            String recipeId = sr.getRecipeKey();
            UserRating rating = ratingGateway.findByUserAndRecipe(USER_ID, recipeId);
            String ratingStr = (rating == null ? "(no rating)" : rating.getStars() + "★");
            listModel.addElement(recipeId + "  " + ratingStr);
        }
    }

    /**
     * Returns the recipe key of the current selection in the left list,
     * or {@code null} if none is selected.
     */
    private String getSelectedRecipeKey() {
        int idx = recipeList.getSelectedIndex();
        if (idx < 0) {
            return null;
        }
        String line = recipeList.getModel().getElementAt(idx);
        return line.split("\\s+")[0];
    }

    /**
     * Seed two demo recipes for USER_ID if none exist yet.
     */
    private static void seedDemoData(UserSavedRecipeAccessObject gateway) {
        if (!gateway.findByUserId(USER_ID).isEmpty()) {
            return; // already has data, do not duplicate
        }

        SavedRecipe r1 = new SavedRecipe(USER_ID, "101");
        r1.setFavourite(false);
        gateway.save(r1);

        SavedRecipe r2 = new SavedRecipe(USER_ID, "102");
        r2.setFavourite(false);
        gateway.save(r2);
    }

    /**
     * Called whenever the RateRecipeViewModel's state changes.
     * We use it to update the small message label and show any errors.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        RateRecipeState state = viewModel.getState();
        String msg = state.getMessage();

        if (msg != null && !msg.isEmpty()) {
            messageLabel.setText(msg);
        }

        if (msg != null && msg.toLowerCase().contains("error")) {
            JOptionPane.showMessageDialog(
                    this,
                    msg,
                    "Rating Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
