package interfaceadapter.search_by_fridge;

import interfaceadapter.AbstractViewModel;

public class SearchByFridgeAbstractViewModel extends AbstractViewModel {

    public static final String VIEW_NAME = "search_by_fridge";

    private SearchByFridgeState state = new SearchByFridgeState();

    public SearchByFridgeAbstractViewModel() {
        super(VIEW_NAME);
    }

    public SearchByFridgeState getState() {
        return state;
    }

    public void setState(SearchByFridgeState newState) {
        this.state = newState;
        firePropertyChanged();
    }

    public void fireStateChanged() {
        firePropertyChanged();
    }
}
