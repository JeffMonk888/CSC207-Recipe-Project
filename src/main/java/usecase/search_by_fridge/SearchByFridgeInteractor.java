package usecase.search_by_fridge;

import domain.entity.RecipePreview;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the Search By Fridge use case.
 *
 * Business rules:
 * - Uses all ingredients in the user's fridge.
 * - Only returns recipes where no ingredients are missing
 *   (missedIngredientCount == 0).
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
        Long userId = inputData.getUserId();
        int number = inputData.getNumber();
        int offset = inputData.getOffset();

        if (userId == null) {
            presenter.presentFailure("User ID cannot be null.");
            return;
        }

        // 1. Get fridge ingredients
        List<String> ingredients = fridgeAccess.getItems(userId);
        if (ingredients.isEmpty()) {
            presenter.presentFailure("Your fridge is empty. Please add some ingredients first.");
            return;
        }

        try {
            List<RecipePreview> apiResults =
                    recipeAccess.getRecipesForIngredients(ingredients, number, offset);

            List<RecipePreview> filtered = new ArrayList<>();
            for (RecipePreview recipe : apiResults) {
                // assuming RecipePreview has missedIngredientCount field
                /**
                 * Add more filter here
                 */

                if (recipe.missedIngredientCount == 0) {
                    filtered.add(recipe);
                }
            }

            int nextOffset = offset + apiResults.size();
            boolean hasMore = apiResults.size() == number;

            SearchByFridgeOutputData outputData =
                    new SearchByFridgeOutputData(filtered, nextOffset, hasMore);

            presenter.presentSuccess(outputData);

        } catch (Exception e) {
            presenter.presentFailure("Failed to fetch recipes: " + e.getMessage());
        }
    }
}
