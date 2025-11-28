package interface_adapter.create_recipe;

import interface_adapter.ViewModel;

public class CreateRecipeViewModel extends ViewModel {
    public static final String VIEW_NAME = "create recipe";
    private CreateRecipeState state = new CreateRecipeState();

    public CreateRecipeViewModel() { super(VIEW_NAME); }

    public CreateRecipeState getState() { return state; }
    public void setState(CreateRecipeState state) { this.state = state; }

    public void firePropertyChanged() {
        super.firePropertyChanged();
    }
}
