package usecase.view_recipe;

import data.api.SpoonacularClient;
import data.api.SpoonacularClient.ApiException;
import data.dto.RecipeInformationDTO;
import data.mapper.RecipeMapper;
import domain.entity.Recipe;

/**
 * Interactor for UC5: View Recipe Details.
 */
public class ViewRecipeInteractor implements ViewRecipeInputBoundary {

    private final SpoonacularClient client;
    private final ViewRecipeOutputBoundary presenter;

    public ViewRecipeInteractor(SpoonacularClient client,
                                ViewRecipeOutputBoundary presenter) {
        this.client = client;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewRecipeInputData inputData) {
        try {
            RecipeInformationDTO dto =
                    client.getRecipeInformation(inputData.getRecipeId(), true);

            Recipe recipe = RecipeMapper.toDomain(dto);

            presenter.presentSuccess(new ViewRecipeOutputData(recipe));

        } catch (ApiException e) {
            presenter.presentFailure(e.getMessage());
        }
    }
}
