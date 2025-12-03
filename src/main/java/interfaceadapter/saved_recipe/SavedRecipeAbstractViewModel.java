package interfaceadapter.saved_recipe;

import interfaceadapter.AbstractViewModel;


public class SavedRecipeAbstractViewModel extends AbstractViewModel {

    public static final String VIEW_NAME = "saved recipes";
    private SavedRecipeState state = new SavedRecipeState();

    public SavedRecipeAbstractViewModel() {
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
