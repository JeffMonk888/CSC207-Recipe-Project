package usecase.common;

import domain.entity.RecipePreview;
import java.util.List;

public interface RecipeByIngredientsAccess {

    List<RecipePreview> getRecipesForIngredients(
            List<String> ingredientList,
            int number,
            int offset
    ) throws Exception;
}
