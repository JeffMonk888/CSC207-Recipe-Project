package interface_adapter.filter_recipes;

import usecase.filter_recipes.FilterRecipesOutputBoundary;
import usecase.filter_recipes.FilterRecipesOutputData;

public class FilterRecipesPresenter implements FilterRecipesOutputBoundary {

    private final FilterRecipesViewModel viewModel;

    public FilterRecipesPresenter(FilterRecipesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(FilterRecipesOutputData outputData) {
        FilterRecipesState state = new FilterRecipesState();
        state.setResults(outputData.getResults());
        viewModel.setState(state);
    }
}
