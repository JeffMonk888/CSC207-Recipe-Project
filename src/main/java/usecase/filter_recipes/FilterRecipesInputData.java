package usecase.filter_recipes;

import domain.entity.Recipe;

import java.util.List;

public class FilterRecipesInputData {

    public enum SortBy { CALORIES, READY_TIME }
    public enum SortOrder { ASC, DESC }

    private final List<Recipe> recipes;
    private final Double maxCalories;     // nullable = no calorie filter
    private final SortBy sortBy;
    private final SortOrder sortOrder;

    public FilterRecipesInputData(List<Recipe> recipes,
                                  Double maxCalories,
                                  SortBy sortBy,
                                  SortOrder sortOrder) {
        this.recipes = recipes;
        this.maxCalories = maxCalories;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public List<Recipe> getRecipes() { return recipes; }
    public Double getMaxCalories() { return maxCalories; }
    public SortBy getSortBy() { return sortBy; }
    public SortOrder getSortOrder() { return sortOrder; }
}
