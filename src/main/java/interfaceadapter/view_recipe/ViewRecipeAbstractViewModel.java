package interfaceadapter.view_recipe;

import interfaceadapter.AbstractViewModel;

public class ViewRecipeAbstractViewModel extends AbstractViewModel {

    public static final String VIEW_NAME = "view_recipe";

    private ViewRecipeState state = new ViewRecipeState();

    public ViewRecipeAbstractViewModel() {
        super(VIEW_NAME);
    }

    public ViewRecipeState getState() {
        return state;
    }
    public void setState(ViewRecipeState state) {
        this.state = state;

        firePropertyChanged();
    }
}
