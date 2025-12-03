package interfaceadapter.create_recipe;

import interfaceadapter.AbstractViewModel;

public class CreateRecipeAbstractViewModel extends AbstractViewModel {
    public static final String VIEW_NAME = "create recipe";
    private CreateRecipeState state = new CreateRecipeState();

    public CreateRecipeAbstractViewModel() { super(VIEW_NAME); }

    public CreateRecipeState getState() { return state; }
    public void setState(CreateRecipeState state) { this.state = state; }

    public void firePropertyChanged() {
        super.firePropertyChanged();
    }
}
