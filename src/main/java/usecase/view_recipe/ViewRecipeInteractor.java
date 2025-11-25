package usecase.view_recipe;

import data.api.SpoonacularClient;
import data.api.SpoonacularClient.ApiException;
import data.dto.RecipeInformationDTO;
import data.mapper.RecipeMapper;
import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;

/**
 * Interactor for UC5: View Recipe Details.
 */
public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final SpoonacularClient client;
    private final RecipeDataAssessObject recipeCache;
    private final ViewRecipeOutputBoundary presenter;

    public ViewRecipeInteractor(SpoonacularClient client, RecipeDataAssessObject recipeCache,
                                ViewRecipeOutputBoundary presenter) {
        this.client = client;
        this.recipeCache = recipeCache;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewRecipeInputData inputData) {
        String key = inputData.getRecipeKey();
        try {
            Recipe recipe;

            if (key.startsWith("a")){
                long apiId = Long.parseLong(key.substring(1));
                RecipeInformationDTO dto =
                        client.getRecipeInformation(apiId, true);

                recipe = RecipeMapper.toDomain(dto);
                presenter.presentSuccess(new ViewRecipeOutputData(recipe));
            } else if (key.startsWith("c")){
                long customId = Long.parseLong(key.substring(1));
                recipe = recipeCache.findById(customId)
                        .orElseThrow(() -> new RuntimeException(
                                "Custom recipe not found in cache: " + customId
                        ));

            } else {
                throw new RuntimeException("Unknown recipe key: " + key);
            }
            presenter.presentSuccess(new ViewRecipeOutputData(recipe));



        } catch (ApiException e) {
            presenter.presentFailure(e.getMessage());
        }
    }
}
