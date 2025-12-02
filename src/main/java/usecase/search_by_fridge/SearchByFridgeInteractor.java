package usecase.search_by_fridge;

import java.util.ArrayList;
import java.util.List;

import domain.entity.RecipePreview;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;

/**
 * Interactor for the Search By Fridge use case.
 *
 * <p>Business rules:
 * - Uses all ingredients in the user's fridge.
 * - Only returns recipes where no ingredients are missing
 *   (missedIngredientCount == 0). </p>
 */
public class SearchByFridgeInteractor implements SearchByFridgeInputBoundary {

    private final FridgeAccess fridgeAccess;
    private final RecipeByIngredientsAccess recipeAccess;
    private final SearchByFridgeOutputBoundary presenter;

    public SearchByFridgeInteractor(FridgeAccess fridgeAccess,
                                    RecipeByIngredientsAccess recipeAccess,
                                    SearchByFridgeOutputBoundary presenter) {
        this.fridgeAccess = fridgeAccess;
        this.recipeAccess = recipeAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchByFridgeInputData inputData) {
        final Long userId = inputData.getUserId();
        final int number = inputData.getNumber();
        final int offset = inputData.getOffset();

        String error = null;
        SearchByFridgeOutputData outputData = null;

        if (userId == null) {
            error = "User ID cannot be null.";

        }
        else {

            // 1. Get fridge ingredients
            final List<String> ingredients = fridgeAccess.getItems(userId);
            if (ingredients.isEmpty()) {
                error = "Your fridge is empty. Please add some ingredients first.";
            }
            try {
                final List<RecipePreview> apiResults =
                        recipeAccess.getRecipesForIngredients(ingredients, number, offset);

                final List<RecipePreview> filtered = new ArrayList<>();
                for (RecipePreview recipe : apiResults) {
                    // assuming RecipePreview has missedIngredientCount field

                    if (recipe.missedIngredientCount == 0) {
                        filtered.add(recipe);
                    }
                }

                final int nextOffset = offset + apiResults.size();
                final boolean hasMore = apiResults.size() == number;

                outputData = new SearchByFridgeOutputData(filtered, nextOffset, hasMore);

                presenter.presentSuccess(outputData);

            }
            catch (Exception exception) {
                error = "Failed to fetch recipes: " + exception.getMessage();
            }
        }
        if (error != null) {
            presenter.presentFailure(error);
        }
        else {
            presenter.presentSuccess(outputData);
        }
    }
}
