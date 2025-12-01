package interface_adapter.filter_recipes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FilterRecipesViewModel {
    public static final String VIEW_NAME = "filter-recipes";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private FilterRecipesState state = new FilterRecipesState();

    public FilterRecipesState getState() {
        return state;
    }

    public void setState(FilterRecipesState state) {
        this.state = state;
        firePropertyChanged();
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
