package interface_adapter.search_by_fridge;

import interface_adapter.ViewModel;

public class SearchByFridgeViewModel extends ViewModel {

    public static final String VIEW_NAME = "search_by_fridge";

    private SearchByFridgeState state = new SearchByFridgeState();

    public SearchByFridgeViewModel() {
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
