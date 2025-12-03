package interfaceadapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Global model to track which view is currently active.
 * The top-level Swing "ViewManager" (a JFrame with CardLayout) listens to this
 * model and switches cards whenever activeViewName changes.
 * Presenters call setActiveViewName(...) after use-cases succeed.
 */
public class ViewManagerModel {

    private String activeViewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Long currentUserId;

    public String getActiveViewName() {
        return activeViewName;
    }

    /**
     * Change the active view and notify listeners.
     * @param activeViewName the new active view name
     */
    public void setActiveViewName(String activeViewName) {
        final String old = this.activeViewName;
        this.activeViewName = activeViewName;
        support.firePropertyChange("activeView", old, activeViewName);
    }

    /**
     * Add a listener to be notified when properties change.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
        // (optional) fire a PropertyChange if you ever want views to react
    }
}
