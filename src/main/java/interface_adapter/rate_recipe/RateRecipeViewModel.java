package interface_adapter.rate_recipe;

import interface_adapter.ViewModel;

/**
 * ViewModel for UC9: Favourite / Rate Recipe.
 *
 * Holds a RateRecipeState instance and exposes it to the Swing view.
 * Whenever the state changes, this ViewModel fires a PropertyChange
 * so that observers (views) can redraw themselves.
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
    }

    @Override
    public void firePropertyChanged() {
        // Just delegate to the base class; using the "state" property name.
        super.firePropertyChanged();
    }
}
