package interfaceadapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for all screen-specific view models.
 * Each concrete ViewModel subclass:
 *  - passes a unique view name into the constructor (used by the ViewManager / CardLayout)
 *  - holds its own state object
 *  - calls firePropertyChanged() whenever that state changes.
 */

public abstract class AbstractViewModel {
    private final String viewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    protected AbstractViewModel(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Returns the logical name of this view, used by the ViewManager and CardLayout.
     * @return the logical name of this view
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Register a listener (typically a Swing view/panel) to be notified
     * whenever this view model's state changes.
     *  @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Remove a previously-added listener.
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Call this whenever the ViewModel's state object has changed.
     */
    protected void firePropertyChanged() {
        support.firePropertyChange("state", null, null);
    }
}
