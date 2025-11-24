package interface_adapter.fridge;

import interface_adapter.ViewModel;

public class FridgeViewModel extends ViewModel {

    public static final String VIEW_NAME = "fridge";

    private FridgeState state = new FridgeState();

    public FridgeViewModel() {
        super(VIEW_NAME);
    }

    public FridgeState getState() {
        return state;
    }

    public void setState(FridgeState state) {
        this.state = state;
        firePropertyChanged();
    }

    public void fireStateChanged() {
        firePropertyChanged();
    }
}
