package interface_adapter.category;

import interface_adapter.ViewModel;

/**
 * View model for category-related screens.
 *
 * It wraps a {@link CategoryState} instance and notifies observers whenever
 * that state changes.
 */
public class CategoryViewModel extends ViewModel {

    public static final String VIEW_NAME = "category";

    private CategoryState state = new CategoryState();

    public CategoryViewModel() {
        super(VIEW_NAME);
    }

    public CategoryState getState() {
        return state;
    }

    public void setState(CategoryState newState) {
        this.state = newState;
        firePropertyChanged();
    }

    /**
     * Convenience method used by presenters after mutating the existing
     * state instance.
     */
    public void fireStateChanged() {
        firePropertyChanged();
    }
}
