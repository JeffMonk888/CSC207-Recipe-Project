package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Global model to track which view is currently active.
 *
 * The top-level Swing "ViewManager" (a JFrame with CardLayout) listens to this
 * model and switches cards whenever activeViewName changes.
 *
 * Presenters call setActiveViewName(...) after use-cases succeed.
 */
public class ViewManagerModel {

    private String activeViewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getActiveViewName() {
        return activeViewName;
    }

    /**
     * Change the active view and notify listeners.
     */
    public void setActiveViewName(String activeViewName) {
        String old = this.activeViewName;
        this.activeViewName = activeViewName;
        support.firePropertyChange("activeView", old, activeViewName);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
