package usecase.saved_recipe;

import data.api.SpoonacularClient;
import data.dto.RecipeInformationDto;
import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import org.junit.jupiter.api.Test;
import usecase.common.MotionForRecipe;
import usecase.delete_saved.*;
import usecase.retrieve_saved.*;
import usecase.save_recipe.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests covering the lifecycle of saved recipes.
 * <p>
 * This class tests {@link SaveRecipeInteractor}, {@link RetrieveSavedInteractor}, 
 * and {@link DeleteSavedInteractor} together to ensure consistent behavior across 
 * different operations.
 * </p>
 */
class SavedRetrieveDeleteRecipeTest {

    // --- Shared Fakes ---

    /** Fake UserDAO storing SavedRecipe links in a list. */
    static class FakeUserDAO implements MotionForRecipe {
        final List<SavedRecipe> db = new ArrayList<>();

        @Override
        public boolean exists(Long userId, String recipeKey) {
            return db.stream().anyMatch(r -> r.getUserId().equals(userId) && r.getRecipeKey().equals(recipeKey));
        }

        @Override
        public void save(SavedRecipe newSave) {
            db.add(newSave);
        }

        @Override
        public ArrayList<SavedRecipe> findByUserId(Long userId) {
            ArrayList<SavedRecipe> result = new ArrayList<>();
            for (SavedRecipe r : db) {
                if (r.getUserId().equals(userId)) result.add(r);
            }
            return result;
        }

        @Override
        public boolean delete(Long userId, String recipeKey) {
            return db.removeIf(r -> r.getUserId().equals(userId) && r.getRecipeKey().equals(recipeKey));
        }
    }

    /** Fake RecipeDAO storing full Recipe details in a map. */
    static class FakeRecipeDAO extends RecipeDataAssessObject {
        final Map<Long, Recipe> memory = new HashMap<>();

        public FakeRecipeDAO() { super("unused.json"); }

        @Override
        public Optional<Recipe> findById(Long recipeId) {
            return Optional.ofNullable(memory.get(recipeId));
        }

        public void addRecipe(Recipe r) { memory.put(r.getId(), r); }
    }

    /** Fake API Client that can be configured to return specific DTOs or throw errors. */
    static class FakeSpoonacular extends SpoonacularClient {
        RecipeInformationDto dtoToReturn;
        boolean shouldThrow = false;

        public FakeSpoonacular() { super("fake_key"); }

        @Override
        public RecipeInformationDto getRecipeInformation(long id, boolean includeNutrition) throws ApiException {
            if (shouldThrow) throw new ApiException("API Error", 500, "Fail");
            return dtoToReturn;
        }
    }

    // --- Presenters ---

    static class CaptureSavePresenter implements SaveRecipeOutputBoundary {
        SaveRecipeOutputData success;
        String error;
        @Override
        public void presentSuccess(SaveRecipeOutputData outputData) { this.success = outputData; }
        @Override
        public void presentFailure(String errorMessage) { this.error = errorMessage; }
    }

    static class CaptureRetrievePresenter implements RetrieveSavedOutputBoundary {
        RetrieveSavedOutputData success;
        String error;
        @Override
        public void presentSuccess(RetrieveSavedOutputData outputData) { this.success = outputData; }
        @Override
        public void presentFailure(String errorMessage) { this.error = errorMessage; }
    }

    static class CaptureDeletePresenter implements DeleteSavedOutputBoundary {
        DeleteSavedOutputData success;
        String error;
        @Override
        public void presentSuccess(DeleteSavedOutputData outputData) { this.success = outputData; }
        @Override
        public void presentFailure(String errorMessage) { this.error = errorMessage; }
    }

    // --- Test Cases ---

    /**
     * Tests the Save Recipe Use Case.
     * <p>
     * <b>Scenario:</b>
     * 1. Save a new recipe -> Success.
     * 2. Try to save the same recipe again -> Failure (Duplicate).
     * </p>
     */
    @Test
    void testSaveRecipe() {
        FakeUserDAO userDAO = new FakeUserDAO();
        CaptureSavePresenter presenter = new CaptureSavePresenter();
        SaveRecipeInteractor interactor = new SaveRecipeInteractor(userDAO, presenter);

        // 1. Test Success
        interactor.execute(new SaveRecipeInputData(1L, "a123"));
        assertNotNull(presenter.success, "First save should succeed");
        assertEquals("a123", presenter.success.getSavedRecipe().getRecipeKey());
        assertTrue(userDAO.exists(1L, "a123"));

        // 2. Test Duplicate Failure
        presenter.success = null;
        interactor.execute(new SaveRecipeInputData(1L, "a123"));
        assertNull(presenter.success, "Duplicate save should not set success data");
        assertEquals("Recipe is already in your collection.", presenter.error);
    }

    /**
     * Tests the Delete Saved Recipe Use Case.
     */
    @Test
    void testDeleteRecipe() {
        FakeUserDAO userDAO = new FakeUserDAO();
        CaptureDeletePresenter presenter = new CaptureDeletePresenter();
        DeleteSavedInteractor interactor = new DeleteSavedInteractor(userDAO, presenter);

        userDAO.save(new SavedRecipe(1L, "c100"));

        // 1. Test Delete Success
        interactor.execute(new DeleteSavedInputData(1L, "c100"));
        assertNotNull(presenter.success);
        assertEquals("c100", presenter.success.getDeletedRecipeKey());
        assertFalse(userDAO.exists(1L, "c100"));

        // 2. Test Delete Non-Existent
        presenter.success = null;
        interactor.execute(new DeleteSavedInputData(1L, "c999"));
        assertEquals("Recipe not found in collection.", presenter.error);

        // 3. Test Delete Failure
        FakeUserDAO failingDAO = new FakeUserDAO() {
            @Override
            public boolean exists(Long u, String k) { return true; } // pretend it exists
            @Override
            public boolean delete(Long u, String k) { return false; } // but fails to delete
        };
        DeleteSavedInteractor failingInteractor = new DeleteSavedInteractor(failingDAO, presenter);
        failingInteractor.execute(new DeleteSavedInputData(1L, "cAnything"));
        assertEquals("Failed to delete recipe.", presenter.error);
    }

    /**
     * Tests the Retrieve Saved Recipes Use Case with mixed data sources.
     * </p>
     */
    @Test
    void testRetrieveSaved() {
        FakeUserDAO userDAO = new FakeUserDAO();
        FakeRecipeDAO recipeDAO = new FakeRecipeDAO();
        FakeSpoonacular client = new FakeSpoonacular();
        CaptureRetrievePresenter presenter = new CaptureRetrievePresenter();

        // Setup Interactor
        RetrieveSavedInteractor interactor;

        // Setup Data
        userDAO.save(new SavedRecipe(1L, "c100"));
        Recipe localRecipe = new Recipe(100L, "Local Pie", "", 1, 10, "", "", "", "c100", null);
        recipeDAO.addRecipe(localRecipe);

        // 2. API Recipe
        userDAO.save(new SavedRecipe(1L, "a200"));
        RecipeInformationDto apiDto = new RecipeInformationDto();
        apiDto.setId(200L);
        apiDto.setTitle("API Burger");
        client.dtoToReturn = apiDto;

        // 3. Broken Recipe (Local)
        userDAO.save(new SavedRecipe(1L, "c999"));
        // 4. Broken Recipe (API)
        userDAO.save(new SavedRecipe(1L, "a500"));
        // Use an anonymous subclass to force a specific exception for ID 500
        client = new FakeSpoonacular() {
            @Override
            public RecipeInformationDto getRecipeInformation(long id, boolean b) throws ApiException {
                if (id == 500) throw new ApiException("Net Error", 500, "");
                return super.getRecipeInformation(id, b);
            }
        };
        client.dtoToReturn = apiDto; // Ensure it still returns valid data for other IDs
        interactor = new RetrieveSavedInteractor(userDAO, recipeDAO, client, presenter);
        interactor.execute(new RetrieveSavedInputData(1L));

        assertNotNull(presenter.success);
        List<Recipe> results = presenter.success.getSavedRecipes();

        // Expecting 2 valid recipes. c999 (missing) and a500 (error) should be gracefully skipped.
        assertEquals(2, results.size());

        // Verify Content
        assertTrue(results.stream().anyMatch(r -> r.getTitle().equals("Local Pie")));
        assertTrue(results.stream().anyMatch(r -> r.getTitle().equals("API Burger")));
    }
}