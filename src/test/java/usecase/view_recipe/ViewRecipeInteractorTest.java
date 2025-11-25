package usecase.view_recipe;

import data.dto.RecipeInformationDTO;
import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewRecipeInteractorTest {

    @Test
    void interactorCallsPresenterOnSuccess() {
        // fake client
        var fakeClient = new FakeClient();
        // capture result
        var capture = new CapturePresenter();

        RecipeDataAssessObject recipeCache =
                new RecipeDataAssessObject("recipe_cache.json");

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(fakeClient, recipeCache, capture);

        interactor.execute(new ViewRecipeInputData("a123L"));

        assertNotNull(capture.lastSuccess);
        Recipe r = capture.lastSuccess.getRecipe();
        assertEquals("Test Recipe", r.getTitle());
    }

    // very small fake SpoonacularClient replacement
    static class FakeClient extends data.api.SpoonacularClient {
        FakeClient() { super("FAKE"); }

        @Override
        public RecipeInformationDTO getRecipeInformation(long id, boolean includeNutrition) {
            RecipeInformationDTO dto = new RecipeInformationDTO();
            dto.id = id;
            dto.title = "Test Recipe";
            // fill in minimal DTO fields here...
            return dto;
        }
    }

    static class CapturePresenter implements ViewRecipeOutputBoundary {
        ViewRecipeOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(ViewRecipeOutputData outputData) {
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            lastError = errorMessage;
        }
    }
}
