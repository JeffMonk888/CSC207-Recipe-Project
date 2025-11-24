package usecase.search_by_ingredients;

public class SearchByIngredientInputData {

    private final String ingredientsQuery;

    public SearchByIngredientInputData(String ingredientsQuery) {
        this.ingredientsQuery = ingredientsQuery;
    }

    public String getIngredientsQuery() {
        return ingredientsQuery;
    }
}
