package view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import interfaceadapter.ViewManagerModel;

/**
 * Top-level UI manager for switching between different screens (views)
 * based on the active view name stored in ViewManagerModel.
 */
public class ViewManager implements PropertyChangeListener {

    private final CardLayout cardLayout;
    private final JPanel viewsPanel;
    private final JFrame window;

    public ViewManager(ViewManagerModel viewManagerModel) {
        viewManagerModel.addPropertyChangeListener(this);

        window = new JFrame("Recipe App");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        viewsPanel = new JPanel(cardLayout);

        window.setContentPane(viewsPanel);
        window.setMinimumSize(new Dimension(900, 600));
        window.setLocationRelativeTo(null);
    }

    /**
     * Register a new view with a corresponding name.
     * @param view the Swing panel to add
     * @param viewName the logical name used by the ViewManager to switch views
     */
    public void addView(JPanel view, String viewName) {
        viewsPanel.add(view, viewName);
    }

    /**
     * Show the window. Call this once after all views have been added.
     */
    public void show() {
        window.pack();
        window.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Listens to ViewManagerModel for "activeVIew" changes
        if (evt.getPropertyName().equals("activeView")) {
            final String newViewName = (String) evt.getNewValue();
            if (newViewName != null) {
                cardLayout.show(viewsPanel, newViewName);
            }
        }
    }
}
