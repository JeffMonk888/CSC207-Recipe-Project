package usecase.create_recipe;

import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import org.junit.jupiter.api.Test;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CreateRecipeInteractor.
 * <p>
 * This class ensures that the recipe creation logic works as expected,
 * specifically testing input validation, ingredient/instruction parsing,
 * and data persistence across the RecipeDAO and UserDAO.
 * </p>
 */
class CreateRecipeInteractorTest {

    // --- Fakes & Mocks ---

    /**
     * Fake implementation of RecipeDataAssessObject to avoid real file during tests.
     * Stores recipes in an in-memory map.
     */
    static class FakeRecipeDAO extends RecipeDataAssessObject {
        final Map<Long, Recipe> memory = new HashMap<>();

        public FakeRecipeDAO() {
            super("unused_path.json");
        }

        @Override
        public void save(Recipe recipe) {
            memory.put(recipe.getId(), recipe);
        }
    }

    /**
     * Fake implementation of MotionForRecipe to simulate user's saved collection.
     */
    static class FakeUserDAO implements MotionForRecipe {
        final List<SavedRecipe> savedList = new ArrayList<>();

        @Override
        public boolean exists(Long userId, String recipeKey) {
            return false;
        }

        @Override
        public void save(SavedRecipe newSave) {
            savedList.add(newSave);
        }

        @Override
        public ArrayList<SavedRecipe> findByUserId(Long userId) {
            return new ArrayList<>();
        }

        @Override
        public boolean delete(Long userId, String recipeKey) {
            return false;
        }
    }

    /**
     * Capture mechanism for the CreateRecipeOutputBoundary.
     * Allows inspection of the success data or failure message passed to the presenter.
     */
    static class CapturePresenter implements CreateRecipeOutputBoundary {
        CreateRecipeOutputData successData;
        String failureMessage;

        @Override
        public void presentSuccess(CreateRecipeOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.failureMessage = errorMessage;
        }
    }

    // --- Tests ---

    /**
     * Tests that providing an empty or null title results in a failure message.
     */
    @Test
    void execute_emptyTitle_presentsFailure() {
        // Arrange
        FakeRecipeDAO recipeDAO = new FakeRecipeDAO();
        FakeUserDAO userDAO = new FakeUserDAO();
        CapturePresenter presenter = new CapturePresenter();
        CreateRecipeInteractor interactor = new CreateRecipeInteractor(recipeDAO, userDAO, presenter);

        // Act & Assert (Null)
        interactor.execute(new CreateRecipeInputData(1L, null, "apple", "eat"));
        assertEquals("Title cannot be empty.", presenter.failureMessage);

        // Act & Assert (Blank)
        presenter.failureMessage = null; // reset
        interactor.execute(new CreateRecipeInputData(1L, "   ", "apple", "eat"));
        assertEquals("Title cannot be empty.", presenter.failureMessage);
    }

    /**
     * Tests the valid input.
     */
    @Test
    void execute_validInput_createsRecipeAndPresentsSuccess() {
        // Arrange
        FakeRecipeDAO recipeDAO = new FakeRecipeDAO();
        FakeUserDAO userDAO = new FakeUserDAO();
        CapturePresenter presenter = new CapturePresenter();
        CreateRecipeInteractor interactor = new CreateRecipeInteractor(recipeDAO, userDAO, presenter);

        Long userId = 123L;
        String title = "My Pancake";
        String ingredients = "flour, egg, milk";
        String instructions = "Mix well.\nCook on pan.";

        // Act
        interactor.execute(new CreateRecipeInputData(userId, title, ingredients, instructions));

        // Assert
        // 1. Check Presenter
        assertNotNull(presenter.successData);
        assertEquals(title, presenter.successData.getRecipeTitle());
        assertNotNull(presenter.successData.getRecipeKey());
        assertTrue(presenter.successData.getRecipeKey().startsWith("c"));

        // 2. Check User DAO (Link saved)
        assertEquals(1, userDAO.savedList.size());
        assertEquals(userId, userDAO.savedList.get(0).getUserId());
        assertEquals(presenter.successData.getRecipeKey(), userDAO.savedList.get(0).getRecipeKey());

        // 3. Check Recipe DAO (Details saved)
        assertFalse(recipeDAO.memory.isEmpty());
        String key = presenter.successData.getRecipeKey();
        Long id = Long.parseLong(key.substring(1));
        Recipe savedRecipe = recipeDAO.memory.get(id);

        assertNotNull(savedRecipe);
        assertEquals(title, savedRecipe.getTitle());

        // Verify ingredients parsing
        assertEquals(3, savedRecipe.getIngredients().size());
        assertEquals("flour", savedRecipe.getIngredients().get(0).getName());
        assertEquals("egg", savedRecipe.getIngredients().get(1).getName());

        // Verify instructions parsing
        assertEquals(2, savedRecipe.getInstructionSteps().size());
        assertEquals("Mix well.", savedRecipe.getInstructionSteps().get(0).getDescription());
    }

    /**
     * Tests creating a recipe without ingredients or instructions.
     */
    @Test
    void execute_emptyIngredientsAndInstructions_createsRecipeWithDefaults() {
        FakeRecipeDAO recipeDAO = new FakeRecipeDAO();
        FakeUserDAO userDAO = new FakeUserDAO();
        CapturePresenter presenter = new CapturePresenter();
        CreateRecipeInteractor interactor = new CreateRecipeInteractor(recipeDAO, userDAO, presenter);

        interactor.execute(new CreateRecipeInputData(1L, "Simple", null, "   "));

        assertNotNull(presenter.successData);
        String key = presenter.successData.getRecipeKey();
        Long id = Long.parseLong(key.substring(1));
        Recipe saved = recipeDAO.memory.get(id);

        assertTrue(saved.getIngredients().isEmpty());
        assertTrue(saved.getInstructionSteps().isEmpty());
    }

    /**
     * Tests edge cases in string parsing logic. Inputs contain consecutive delimiters (e.g., "a,,b") or extra newlines.
     */
    @Test
    void execute_parsingEdgeCases() {
        FakeRecipeDAO recipeDAO = new FakeRecipeDAO();
        FakeUserDAO userDAO = new FakeUserDAO();
        CapturePresenter presenter = new CapturePresenter();
        CreateRecipeInteractor interactor = new CreateRecipeInteractor(recipeDAO, userDAO, presenter);

        String ingredients = "apple,,banana";
        String instructions = "\nstep1\n\nstep2";

        interactor.execute(new CreateRecipeInputData(1L, "Edge Case", ingredients, instructions));

        String key = presenter.successData.getRecipeKey();
        Long id = Long.parseLong(key.substring(1));
        Recipe saved = recipeDAO.memory.get(id);

        assertEquals(2, saved.getIngredients().size());
        assertEquals("apple", saved.getIngredients().get(0).getName());
        assertEquals("banana", saved.getIngredients().get(1).getName());

        assertEquals(2, saved.getInstructionSteps().size());
        assertEquals("step1", saved.getInstructionSteps().get(0).getDescription());
        assertEquals("step2", saved.getInstructionSteps().get(1).getDescription());
    }
}
