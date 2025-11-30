package interface_adapter.filter_recipes;

import domain.entity.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FilterRecipesState {
    private List<Recipe> results = new ArrayList<>();

    public List<Recipe> getResults() {
        return results;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}
