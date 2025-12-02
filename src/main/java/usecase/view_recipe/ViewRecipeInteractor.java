package usecase.view_recipe;

import data.api.SpoonacularClient;
import data.api.SpoonacularClient.ApiException;
import data.dto.RecipeInformationDto;
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
        final String key = inputData.getRecipeKey();
        try {
            final Recipe recipe;

            if (key.startsWith("a")) {
                final long apiId = Long.parseLong(key.substring(1));
                final RecipeInformationDto dto =
                        client.getRecipeInformation(apiId, true);

                recipe = RecipeMapper.toDomain(dto);
                presenter.presentSuccess(new ViewRecipeOutputData(recipe));
            }
            else if (key.startsWith("c")) {
                final long customId = Long.parseLong(key.substring(1));
                recipe = recipeCache.findById(customId)
                        .orElseThrow(() -> {
                            return new RuntimeException(
                                    "Custom recipe not found in cache: " + customId
                            );
                        });

            }
            else {
                throw new RuntimeException("Unknown recipe key: " + key);
            }
            presenter.presentSuccess(new ViewRecipeOutputData(recipe));
        }
        catch (ApiException apiException) {
            presenter.presentFailure(apiException.getMessage());
        }
    }
}
