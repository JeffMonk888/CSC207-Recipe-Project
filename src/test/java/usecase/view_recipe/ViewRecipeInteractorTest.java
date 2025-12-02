package usecase.view_recipe;

import data.api.SpoonacularClient;
import data.api.SpoonacularClient.ApiException;
import data.dto.RecipeInformationDto;
import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.NutritionInfo;
import domain.entity.Recipe;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ViewRecipeInteractor.
 * These tests are designed to achieve 100% line coverage of the interactor.
 */
class ViewRecipeInteractorTest {

    /**
     * Simple test presenter that records calls for inspection.
     */
    private static class FakePresenter implements ViewRecipeOutputBoundary {
        ViewRecipeOutputData lastSuccess;
        String lastFailure;
        int successCount = 0;
        int failureCount = 0;

        @Override
        public void presentSuccess(ViewRecipeOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastFailure = errorMessage;
        }
    }

    /**
     * Test double for SpoonacularClient that either returns a fixed DTO
     * or throws an ApiException.
     */
    private static class FakeSpoonacularClient extends SpoonacularClient {

        final private RecipeInformationDto dtoToReturn;
        private ApiException exceptionToThrow;

        FakeSpoonacularClient(RecipeInformationDto dtoToReturn) {
            super("TEST_KEY");
            this.dtoToReturn = dtoToReturn;
        }

        static FakeSpoonacularClient thatThrows(ApiException ex) {
            FakeSpoonacularClient client = new FakeSpoonacularClient(null);
            client.exceptionToThrow = ex;
            return client;
        }

        @Override
        public RecipeInformationDto getRecipeInformation(long id, boolean includeNutrition)
                throws ApiException {

            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return dtoToReturn;
        }
    }

    // ---------- helpers ----------

    private static RecipeInformationDto createMinimalDto(long id) {
        RecipeInformationDto dto = new RecipeInformationDto();
        dto.setId(id);
        dto.setTitle("API Recipe");
        dto.setServings(2);
        dto.setReadyInMinutes(30);
        dto.setSourceName("Source");
        dto.setSourceUrl("http://example.com");
        dto.setImage("http://example.com/image.jpg");
        dto.setCalories(100.0);
        return dto;
    }

    private static Recipe createCustomRecipe(long id) {
        NutritionInfo nutrition = new NutritionInfo(
                null,
                200.0,
                "10g",
                "5g",
                "20g"
        );
        return new Recipe(
                id,
                "Custom Recipe",
                "",
                1,
                10,
                "Custom Source",
                "http://example.com/custom",
                "http://example.com/custom.jpg",
                "c" + id,
                nutrition
        );
    }

    private static RecipeDataAssessObject createCacheWith(Recipe recipe) throws Exception {
        File tmp = File.createTempFile("recipe-cache", ".json");
        tmp.deleteOnExit();
        RecipeDataAssessObject cache = new RecipeDataAssessObject(tmp.getAbsolutePath());
        cache.save(recipe);
        return cache;
    }

    // ---------- tests ----------

    @Test
    void execute_fetchesFromApi_whenKeyStartsWithA() throws Exception {
        long apiId = 123L;
        RecipeInformationDto dto = createMinimalDto(apiId);
        FakeSpoonacularClient client = new FakeSpoonacularClient(dto);
        // cache is unused in this branch, but must be non-null
        RecipeDataAssessObject cache =
                createCacheWith(createCustomRecipe(1L));
        FakePresenter presenter = new FakePresenter();

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(client, cache, presenter);

        interactor.execute(new ViewRecipeInputData("a" + apiId));

        // In the "a" branch, presentSuccess is called once inside the branch
        // and once again after the if/else chain.
        assertEquals(2, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals("API Recipe",
                presenter.lastSuccess.getRecipe().getTitle());
    }

    @Test
    void execute_readsFromCache_whenKeyStartsWithC() throws Exception {
        long customId = 42L;
        Recipe customRecipe = createCustomRecipe(customId);
        RecipeDataAssessObject cache = createCacheWith(customRecipe);
        FakeSpoonacularClient client =
                new FakeSpoonacularClient(createMinimalDto(999L)); // unused
        FakePresenter presenter = new FakePresenter();

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(client, cache, presenter);

        interactor.execute(new ViewRecipeInputData("c" + customId));

        assertEquals(1, presenter.successCount);  // only the final presentSuccess
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals("Custom Recipe",
                presenter.lastSuccess.getRecipe().getTitle());
    }

    @Test
    void execute_throwsRuntimeException_whenCustomRecipeNotFound() throws Exception {
        // empty cache
        File tmp = File.createTempFile("recipe-cache-empty", ".json");
        tmp.deleteOnExit();
        RecipeDataAssessObject cache = new RecipeDataAssessObject(tmp.getAbsolutePath());
        FakeSpoonacularClient client =
                new FakeSpoonacularClient(createMinimalDto(1L));
        FakePresenter presenter = new FakePresenter();

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(client, cache, presenter);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> interactor.execute(new ViewRecipeInputData("c999"))
        );

        assertTrue(ex.getMessage().contains("Custom recipe not found"));
        assertEquals(0, presenter.successCount);
        assertEquals(0, presenter.failureCount);
    }

    @Test
    void execute_throwsRuntimeException_forUnknownKeyPrefix() throws Exception {
        FakeSpoonacularClient client =
                new FakeSpoonacularClient(createMinimalDto(1L));
        RecipeDataAssessObject cache =
                createCacheWith(createCustomRecipe(1L));
        FakePresenter presenter = new FakePresenter();

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(client, cache, presenter);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> interactor.execute(new ViewRecipeInputData("x123"))
        );

        assertTrue(ex.getMessage().contains("Unknown recipe key"));
        assertEquals(0, presenter.successCount);
        assertEquals(0, presenter.failureCount);
    }

    @Test
    void execute_presentsFailure_whenApiExceptionThrown() throws Exception {
        ApiException apiException = new ApiException("API failure", 500, null);
        FakeSpoonacularClient client = FakeSpoonacularClient.thatThrows(apiException);
        RecipeDataAssessObject cache =
                createCacheWith(createCustomRecipe(1L));
        FakePresenter presenter = new FakePresenter();

        ViewRecipeInteractor interactor =
                new ViewRecipeInteractor(client, cache, presenter);

        interactor.execute(new ViewRecipeInputData("a123"));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("API failure", presenter.lastFailure);
    }
}
