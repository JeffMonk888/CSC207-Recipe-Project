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
            // 1) call API
            RecipeInformationDTO dto =
                    client.getRecipeInformation(inputData.getRecipeId(), true);

            // 2) map to domain
            Recipe recipe = RecipeMapper.toDomain(dto);

            // 3) send to presenter
            presenter.presentSuccess(new ViewRecipeOutputData(recipe));

        } catch (ApiException e) {
            // Could make this error message nicer later
            presenter.presentFailure(e.getMessage());
        }
    }
}
