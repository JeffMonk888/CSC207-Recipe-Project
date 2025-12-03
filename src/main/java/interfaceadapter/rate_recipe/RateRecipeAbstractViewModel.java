package interfaceadapter.rate_recipe;

import interfaceadapter.AbstractViewModel;

/**
 * ViewModel for UC9: Favourite / Rate Recipe.
 *
 * Holds a RateRecipeState instance and exposes it to the Swing view.
 * Whenever the state changes, this ViewModel fires a PropertyChange
 * so that observers (views) can redraw themselves.
 */
public class RateRecipeAbstractViewModel extends AbstractViewModel {

    public static final String VIEW_NAME = "rate recipe";

    private RateRecipeState state = new RateRecipeState();

    public RateRecipeAbstractViewModel() {
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
