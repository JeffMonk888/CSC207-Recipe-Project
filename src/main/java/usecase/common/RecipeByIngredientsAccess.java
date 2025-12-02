package usecase.common;

import java.util.List;

import domain.entity.RecipePreview;

/**
 * Interface for retrieving recipes based on a list of ingredient.
 *
 * <p>This interface abstracts the mechanism used to query recipes that match
 * a given set of ingredients. Implementations my retrieve data form an external API,
 * a database, or other source, while the user case layer depends only on this interface.</p>
 */
public interface RecipeByIngredientsAccess {
    /**
     * Returns a list of recipes that match the provided ingredients.
     *
     * @param ingredientList the list of ingredient names to search with
     * @param number the maximum number of recipe results to return
     * @param offset the starting index for pagination of results
     * @return a list of Preview Recipe objects representing recipes that
     *         match the provided ingredients
     * @throws Exception if the underlying data retrieval fails
     */
    List<RecipePreview> getRecipesForIngredients(
            List<String> ingredientList,
            int number,
            int offset
    ) throws Exception;
}
