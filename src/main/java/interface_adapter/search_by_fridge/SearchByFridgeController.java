package interface_adapter.search_by_fridge;

import usecase.search_by_fridge.SearchByFridgeInputBoundary;
import usecase.search_by_fridge.SearchByFridgeInputData;

public class SearchByFridgeController {

    private final SearchByFridgeInputBoundary searchByFridgeInteractor;

    public SearchByFridgeController(SearchByFridgeInputBoundary searchByFridgeInteractor) {
        this.searchByFridgeInteractor = searchByFridgeInteractor;
    }

    public void search(Long userId, int number, int offset) {
        SearchByFridgeInputData input =
                new SearchByFridgeInputData(userId, number, offset);

        searchByFridgeInteractor.execute(input);
    }
}
