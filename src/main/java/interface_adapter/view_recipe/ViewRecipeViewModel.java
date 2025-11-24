package interface_adapter.view_recipe;

import interface_adapter.ViewModel;

public class ViewRecipeViewModel extends ViewModel {

    public static final String VIEW_NAME = "view_recipe";

    private ViewRecipeState state = new ViewRecipeState();

    public ViewRecipeViewModel() {
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
