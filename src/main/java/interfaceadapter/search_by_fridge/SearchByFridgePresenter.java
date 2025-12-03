package interfaceadapter.search_by_fridge;

import usecase.search_by_fridge.SearchByFridgeOutputBoundary;
import usecase.search_by_fridge.SearchByFridgeOutputData;

public class SearchByFridgePresenter implements SearchByFridgeOutputBoundary {

    private final SearchByFridgeAbstractViewModel viewModel;

    public SearchByFridgePresenter(SearchByFridgeAbstractViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(SearchByFridgeOutputData outputData) {
        SearchByFridgeState state = viewModel.getState();

        state.setRecipes(outputData.getRecipes());
        state.setOffset(outputData.getNextOffset());
        state.setHasMore(outputData.hasMore());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    @Override
    public void presentFailure(String errorMessage) {
        SearchByFridgeState state = viewModel.getState();
        state.setErrorMessage(errorMessage);

        viewModel.fireStateChanged();
    }
}
