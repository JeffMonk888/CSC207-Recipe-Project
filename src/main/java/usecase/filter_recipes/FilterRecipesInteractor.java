package usecase.filter_recipes;

import domain.entity.NutritionInfo;
import domain.entity.Recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static usecase.filter_recipes.FilterRecipesInputData.SortBy;
import static usecase.filter_recipes.FilterRecipesInputData.SortOrder;

public class FilterRecipesInteractor implements FilterRecipesInputBoundary {

    private final FilterRecipesOutputBoundary presenter;

    public FilterRecipesInteractor(FilterRecipesOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterRecipesInputData inputData) {
        List<Recipe> base = inputData.getRecipes();
        if (base == null) {
            presenter.present(new FilterRecipesOutputData(List.of()));
            return;
        }

        // copy so we don't mutate the original list
        List<Recipe> filtered = new ArrayList<>(base);

        // filter by max calories (if provided)
        Double maxCal = inputData.getMaxCalories();
        if (maxCal != null) {
            filtered.removeIf(r -> {
                NutritionInfo n = r.getNutritionInfo();
                return n == null || n.getCalories() == null || n.getCalories() > maxCal;
            });
        }

        // sort
        Comparator<Recipe> comparator = buildComparator(inputData.getSortBy());

        if (inputData.getSortOrder() == SortOrder.DESC) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);

        presenter.present(new FilterRecipesOutputData(filtered));
    }

    private Comparator<Recipe> buildComparator(SortBy sortBy) {
        return switch (sortBy) {
            case CALORIES -> Comparator.comparing(
                    r -> {
                        NutritionInfo n = r.getNutritionInfo();
                        return (n == null || n.getCalories() == null)
                                ? Double.MAX_VALUE
                                : n.getCalories();
                    }
            );
            case READY_TIME -> Comparator.comparing(
                    r -> r.getPrepTimeInMinutes() == null
                            ? Integer.MAX_VALUE
                            : r.getPrepTimeInMinutes()
            );
        };
    }
}
