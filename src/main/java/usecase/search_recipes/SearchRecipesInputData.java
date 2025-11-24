package usecase.search_recipes;

public class SearchRecipesInputData {

    private final String ingredientsQuery;

    public SearchRecipesInputData(String ingredientsQuery) {
        this.ingredientsQuery = ingredientsQuery;
    }

    public String getIngredientsQuery() {
        return ingredientsQuery;
    }
}
