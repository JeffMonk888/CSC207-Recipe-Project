package usecase.saved_recipe;

import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import usecase.common.MotionForRecipe;
import usecase.delete_saved.*;
import usecase.retrieve_saved.*;
import usecase.save_recipe.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SavedRecipeInteractorsTest {

    @TempDir
    Path tempDir;

    private MotionForRecipe userSavedRecipeDAO;
    private RecipeDataAssessObject recipeDAO;

    private final Long testUserId = 1L;
    private final Long testRecipeId1 = 100L;
    private final Long testRecipeId2 = 200L;


    @BeforeEach
    void setUp() throws IOException {
        File linksCsv = tempDir.resolve("temp_links.csv").toFile();
        File cacheJson = tempDir.resolve("temp_cache.json").toFile();

        userSavedRecipeDAO = new UserSavedRecipeAccessObject(linksCsv.getAbsolutePath());
        recipeDAO = new RecipeDataAssessObject(cacheJson.getAbsolutePath());
    }


    private Recipe createTestRecipe(Long id) {
        return new Recipe(id, "Test Recipe " + id, "Desc", 2, 10,
                "Source", "url", "img", String.valueOf(id), null);
    }


    @Test
    void testSaveRecipeSuccess() {
        var presenter = new CaptureSavePresenter();
        SaveRecipeInputBoundary interactor = new SaveRecipeInteractor(userSavedRecipeDAO, presenter);

        interactor.execute(new SaveRecipeInputData(testUserId, testRecipeId1));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        assertTrue(userSavedRecipeDAO.exists(testUserId, testRecipeId1));
        assertEquals(testRecipeId1, presenter.lastSuccess.getSavedRecipe().getRecipeId());
    }


    @Test
    void testRetrieveSavedRecipesSuccess() {

        Recipe recipe1 = createTestRecipe(testRecipeId1);
        Recipe recipe2 = createTestRecipe(testRecipeId2);
        recipeDAO.save(recipe1);
        recipeDAO.save(recipe2);

        userSavedRecipeDAO.save(new SavedRecipe(testUserId, testRecipeId1));
        userSavedRecipeDAO.save(new SavedRecipe(testUserId, testRecipeId2));
        userSavedRecipeDAO.save(new SavedRecipe(2L, 300L));

        var presenter = new CaptureRetrievePresenter();
        RetrieveSavedInputBoundary interactor = new RetrieveSavedInteractor(userSavedRecipeDAO, recipeDAO, presenter);


        interactor.execute(new RetrieveSavedInputData(testUserId));


        assertNotNull(presenter.lastSuccess);
        List<Recipe> results = presenter.lastSuccess.getSavedRecipes();


        assertEquals(2, results.size());


        boolean found100 = results.stream()
                .anyMatch(recipe -> recipe.getTitle().equals("Test Recipe 100"));
        boolean found200 = results.stream()
                .anyMatch(recipe -> recipe.getTitle().equals("Test Recipe 200"));

        assertTrue(found100, "include 'Test Recipe 100'");
        assertTrue(found200, "include 'Test Recipe 200'");

    }


    @Test
    void testDeleteSavedRecipeSuccess() {

        userSavedRecipeDAO.save(new SavedRecipe(testUserId, testRecipeId1));
        assertTrue(userSavedRecipeDAO.exists(testUserId, testRecipeId1), "not saved recipe");

        var presenter = new CaptureDeletePresenter();
        DeleteSavedInputBoundary interactor = new DeleteSavedInteractor(userSavedRecipeDAO, presenter);

        interactor.execute(new DeleteSavedInputData(testUserId, testRecipeId1));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        assertEquals(testRecipeId1, presenter.lastSuccess.getDeletedRecipeId());

        assertFalse(userSavedRecipeDAO.exists(testUserId, testRecipeId1));
    }

    // helper function

    static class CaptureSavePresenter implements SaveRecipeOutputBoundary {
        SaveRecipeOutputData lastSuccess;
        String lastError;
        @Override public void presentSuccess(SaveRecipeOutputData outputData) { this.lastSuccess = outputData; }
        @Override public void presentFailure(String errorMessage) { this.lastError = errorMessage; }
    }

    static class CaptureRetrievePresenter implements RetrieveSavedOutputBoundary {
        RetrieveSavedOutputData lastSuccess;
        @Override public void presentSuccess(RetrieveSavedOutputData outputData) { this.lastSuccess = outputData; }
    }

    static class CaptureDeletePresenter implements DeleteSavedOutputBoundary {
        DeleteSavedOutputData lastSuccess;
        String lastError;
        @Override public void presentSuccess(DeleteSavedOutputData outputData) { this.lastSuccess = outputData; }
        @Override public void presentFailure(String errorMessage) { this.lastError = errorMessage; }
    }
}