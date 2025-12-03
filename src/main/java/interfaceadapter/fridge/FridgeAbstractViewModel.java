package interfaceadapter.fridge;

import interfaceadapter.AbstractViewModel;

public class FridgeAbstractViewModel extends AbstractViewModel {

    public static final String VIEW_NAME = "fridge";

    private FridgeState state = new FridgeState();

    public FridgeAbstractViewModel() {
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
