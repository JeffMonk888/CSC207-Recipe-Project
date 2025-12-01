package interface_adapter.rate_recipe;

import interface_adapter.ViewModel;

/**
 * ViewModel for UC9: Favourite / Rate Recipe.
 *
 * A Swing view should observe this ViewModel and repaint itself when
 * {@link #fireStateChanged()} is called.
 */
public class RateRecipeViewModel extends ViewModel {

    public static final String VIEW_NAME = "rate recipe";

    private RateRecipeState state = new RateRecipeState();

    public RateRecipeViewModel() {
        super(VIEW_NAME);
    }

    public RateRecipeState getState() {
        return state;
    }

    public void setState(RateRecipeState state) {
        this.state = state;
        firePropertyChanged();
    }

    /**
     * Convenience method used by presenters when they mutate the existing
     * state instance.
     */
    public void fireStateChanged() {
        firePropertyChanged();
    }
}
