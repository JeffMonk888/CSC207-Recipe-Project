package interface_adapter.saved_recipe;

import interface_adapter.ViewModel;


public class SavedRecipeViewModel extends ViewModel {

    public static final String VIEW_NAME = "saved recipes";
    private SavedRecipeState state = new SavedRecipeState();

    public SavedRecipeViewModel() {
        super(VIEW_NAME);
    }

    public void setState(SavedRecipeState state) {
        this.state = state;
    }

    public SavedRecipeState getState() {
        return state;
    }

    @Override
    public void firePropertyChanged() {
        super.firePropertyChanged();
    }
}
