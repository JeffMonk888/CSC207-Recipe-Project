package interface_adapter.filter_recipes;

import domain.entity.Recipe;
import usecase.filter_recipes.FilterRecipesInputBoundary;
import usecase.filter_recipes.FilterRecipesInputData;

import java.util.List;

public class FilterRecipesController {
    private final FilterRecipesInputBoundary interactor;

    public FilterRecipesController(FilterRecipesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(List<Recipe> recipes,
                        Double maxCalories,
                        FilterRecipesInputData.SortBy sortBy,
                        FilterRecipesInputData.SortOrder sortOrder) {

        FilterRecipesInputData inputData =
                new FilterRecipesInputData(recipes, maxCalories, sortBy, sortOrder);
        interactor.execute(inputData);
    }
}
