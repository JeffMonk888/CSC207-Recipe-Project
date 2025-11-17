package usecase.filter_recipes;

import domain.entity.Recipe;

import java.util.List;

public class FilterRecipesOutputData {
    private final java.util.List<Recipe> results;

    public FilterRecipesOutputData(List<Recipe> results) {
        this.results = results;
    }

    public List<Recipe> getResults() {
        return results;
    }
}
