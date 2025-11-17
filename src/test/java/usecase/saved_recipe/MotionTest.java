package usecase.saved_recipe;

// Imports for testing
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Domain and Use Case imports
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;
import usecase.save_recipe.*;
import usecase.retrieve_saved.*;
import usecase.delete_saved.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

class SavedRecipeInteractorsTest {

    private FakeGateway fakeGateway;
    private Long testUserId = 1L;
    private Long testRecipeId1 = 100L;
    private Long testRecipeId2 = 200L;

    /**
     * Sets up a new, empty FakeGateway before each test.
     */
    @BeforeEach
    void setUp() {
        fakeGateway = new FakeGateway();
    }

    /**
     * Tests the success scenario for the Save Recipe use case (UC6).
     */
    @Test
    void testSaveRecipeSuccess() {
        // 1. Arrange
        var presenter = new CaptureSavePresenter();
        SaveRecipeInputBoundary interactor = new SaveRecipeInteractor(fakeGateway, presenter);
        interactor.execute(new SaveRecipeInputData(testUserId, testRecipeId1));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        SavedRecipe saved = presenter.lastSuccess.getSavedRecipe();
        assertEquals(testUserId, saved.getUserId());
        assertEquals(testRecipeId1, saved.getRecipeId());
        assertNotNull(saved.getId());

        assertTrue(fakeGateway.exists(testUserId, testRecipeId1));
    }

    @Test
    void testRetrieveSavedRecipesSuccess() {
        fakeGateway.save(new SavedRecipe(testUserId, testRecipeId1));
        fakeGateway.save(new SavedRecipe(testUserId, testRecipeId2));
        fakeGateway.save(new SavedRecipe(2L, 300L)); // Belongs to another user

        var presenter = new CaptureRetrievePresenter();
        RetrieveSavedInputBoundary interactor = new RetrieveSavedInteractor(fakeGateway, presenter);

        interactor.execute(new RetrieveSavedInputData(testUserId));

        assertNotNull(presenter.lastSuccess);
        ArrayList<SavedRecipe> results = presenter.lastSuccess.getSavedRecipes();

        assertEquals(2, results.size());

        assertTrue(results.stream().anyMatch(r -> r.getRecipeId().equals(testRecipeId1)));
        assertTrue(results.stream().anyMatch(r -> r.getRecipeId().equals(testRecipeId2)));
    }


    @Test
    void testDeleteSavedRecipeSuccess() {

        fakeGateway.save(new SavedRecipe(testUserId, testRecipeId1));
        assertTrue(fakeGateway.exists(testUserId, testRecipeId1));

        var presenter = new CaptureDeletePresenter();
        DeleteSavedInputBoundary interactor = new DeleteSavedInteractor(fakeGateway, presenter);


        interactor.execute(new DeleteSavedInputData(testUserId, testRecipeId1));


        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        assertEquals(testRecipeId1, presenter.lastSuccess.getDeletedRecipeId());

        assertFalse(fakeGateway.exists(testUserId, testRecipeId1));
    }


    static class FakeGateway implements MotionForRecipe {
        private final Map<String, SavedRecipe> store = new HashMap<>();
        private final AtomicLong idCounter = new AtomicLong(1L);


        private String key(Long userId, Long recipeId) {
            return userId + ":" + recipeId;
        }

        @Override
        public boolean exists(Long userId, Long recipeId) {
            return store.containsKey(key(userId, recipeId));
        }

        @Override
        public void save(SavedRecipe newSave) {
            if (newSave.getId() == null) {
                newSave.setId(idCounter.getAndIncrement());
            }
            store.put(key(newSave.getUserId(), newSave.getRecipeId()), newSave);
        }


        @Override
        public ArrayList<SavedRecipe> findByUserId(Long userId) {
            ArrayList<SavedRecipe> results = new ArrayList<>();
            for (SavedRecipe saved : store.values()) {
                if (Objects.equals(saved.getUserId(), userId)) {
                    results.add(saved);
                }
            }
            return results;
        }

        @Override
        public boolean delete(Long userId, Long recipeId) {
            SavedRecipe removed = store.remove(key(userId, recipeId));
            return removed != null;
        }
    }


    static class CaptureSavePresenter implements SaveRecipeOutputBoundary {
        SaveRecipeOutputData lastSuccess;
        String lastError;


        @Override
        public void presentSuccess(SaveRecipeOutputData outputData) {
            this.lastSuccess = outputData;
        }


        @Override
        public void presentFailure(String errorMessage) {
            this.lastError = errorMessage;
        }
    }


    static class CaptureRetrievePresenter implements RetrieveSavedOutputBoundary {
        RetrieveSavedOutputData lastSuccess;


        @Override
        public void presentSuccess(RetrieveSavedOutputData outputData) {
            this.lastSuccess = outputData;
        }
    }


    static class CaptureDeletePresenter implements DeleteSavedOutputBoundary {
        DeleteSavedOutputData lastSuccess;
        String lastError;


        @Override
        public void presentSuccess(DeleteSavedOutputData outputData) {
            this.lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.lastError = errorMessage;
        }
    }
}
