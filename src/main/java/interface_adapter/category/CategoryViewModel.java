package interface_adapter.category;

import interface_adapter.ViewModel;

/**
 * ViewModel for UC10: Category management.
 *
 * Holds a CategoryState instance and exposes it to the Swing view.
 * Whenever the state changes, this ViewModel fires a PropertyChange
 * so that observers (views) can redraw themselves.
 */
public class CategoryViewModel extends ViewModel {

    /** Logical name for this view, used by the ViewManager / CardLayout. */
    public static final String VIEW_NAME = "category";

    private CategoryState state = new CategoryState();

    public CategoryViewModel() {
        super(VIEW_NAME);
    }

    public CategoryState getState() {
        return state;
    }

    /**
     * Replace the entire state object and notify listeners.
     */
    public void setState(CategoryState state) {
        this.state = state;
        firePropertyChanged();
    }

    /**
     * Call this when *fields inside* the current state object change.
     */
    public void fireStateChanged() {
        firePropertyChanged();
    }
}
