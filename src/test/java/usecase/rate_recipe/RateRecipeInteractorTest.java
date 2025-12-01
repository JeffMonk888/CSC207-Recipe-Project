package usecase.rate_recipe;

import domain.entity.UserRating;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC9 Favourite / Rate Recipe.
 *
 * These tests are written against the current design:
 *  - stars are doubles in [0.0, 5.0] with step 0.5
 *  - clearRating mode explicitly deletes the rating
 *  - in normal mode, stars must be non-null and valid
 */
class RateRecipeInteractorTest {

    @Test
    void clearRatingDeletesExistingRatingAndSignalsRemoved() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        long userId = 1L;
        String recipeId = "101";

        // Seed an existing rating
        UserRating existing = new UserRating(userId, recipeId, 2.5);
        gateway.save(existing);

        // Execute clear mode
        RateRecipeInputData input =
                RateRecipeInputData.forClear(userId, recipeId);
        interactor.execute(input);

        // Rating should be deleted
        assertNull(gateway.findByUserAndRecipe(userId, recipeId));

        // Presenter should receive a "removed" success with null rating
        assertNotNull(presenter.lastSuccess);
        assertTrue(presenter.lastSuccess.isRemoved());
        assertNull(presenter.lastSuccess.getRating());
        assertNull(presenter.lastError);

        // Gateway delete should be called with correct ids
        assertEquals(userId, gateway.getLastDeletedUserId());
        assertEquals(recipeId, gateway.getLastDeletedRecipeId());
    }

    @Test
    void nullStarsWhenNotClearCausesFailureAndNoSaveOrDelete() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        // clearRating is false but stars is null
        RateRecipeInputData input =
                new RateRecipeInputData(1L, "201", null, false);

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertEquals(
                "Stars cannot be null unless clearRating is true.",
                presenter.lastError
        );

        // No rating should be saved or deleted
        assertNull(gateway.findByUserAndRecipe(1L, "201"));
        assertNull(gateway.getLastDeletedUserId());
        assertNull(gateway.getLastDeletedRecipeId());
    }

    @Test
    void invalidStarsCauseFailureAndNoSaveOrDelete() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        // 4.3 is not a multiple of 0.5, so it is invalid
        RateRecipeInputData input =
                RateRecipeInputData.forRating(1L, "301", 4.3);

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertEquals(
                "Rating must be between 0.0 and 5.0 in steps of 0.5.",
                presenter.lastError
        );

        assertNull(gateway.findByUserAndRecipe(1L, "301"));
        assertNull(gateway.getLastDeletedUserId());
        assertNull(gateway.getLastDeletedRecipeId());
    }

    @Test
    void firstTimeValidRatingCreatesNewRatingAndSavesIt() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        long userId = 2L;
        String recipeId = "401";
        double stars = 4.0;

        RateRecipeInputData input =
                RateRecipeInputData.forRating(userId, recipeId, stars);
        interactor.execute(input);

        // A new rating should be stored in the gateway
        UserRating saved = gateway.findByUserAndRecipe(userId, recipeId);
        assertNotNull(saved);
        assertEquals(userId, saved.getUserId());
        assertEquals(recipeId, saved.getRecipeId());
        assertEquals(stars, saved.getStars(), 1e-9);
        assertNotNull(saved.getUpdatedAt());

        // Presenter should receive the same rating and removed == false
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());
        assertNotNull(presenter.lastSuccess.getRating());
        assertEquals(stars, presenter.lastSuccess.getRating().getStars(), 1e-9);
        assertNull(presenter.lastError);
    }

    @Test
    void updatingExistingRatingChangesStarsAndTimestamp() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        long userId = 3L;
        String recipeId = "501";

        // Existing rating with an old timestamp
        UserRating existing = new UserRating(userId, recipeId, 1.0);
        Instant oldTime = existing.getUpdatedAt();
        gateway.save(existing);

        // Update stars to a new valid value
        RateRecipeInputData input =
                RateRecipeInputData.forRating(userId, recipeId, 5.0);
        interactor.execute(input);

        UserRating stored = gateway.findByUserAndRecipe(userId, recipeId);
        assertNotNull(stored);
        assertEquals(5.0, stored.getStars(), 1e-9);
        assertNotNull(stored.getUpdatedAt());
        assertNotEquals(oldTime, stored.getUpdatedAt());

        // Presenter should also get the updated rating
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());
        assertEquals(5.0, presenter.lastSuccess.getRating().getStars(), 1e-9);
        assertNull(presenter.lastError);
    }

    @Test
    void inputDataFactoriesAndGettersWorkAsDocumented() {
        long userId = 10L;
        String recipeId = "X123";

        // forRating factory
        RateRecipeInputData ratingInput =
                RateRecipeInputData.forRating(userId, recipeId, 3.5);
        assertEquals(userId, ratingInput.getUserId());
        assertEquals(recipeId, ratingInput.getRecipeId());
        assertEquals(3.5, ratingInput.getStars(), 1e-9);
        assertFalse(ratingInput.isClearRating());

        // forClear factory
        RateRecipeInputData clearInput =
                RateRecipeInputData.forClear(userId, recipeId);
        assertEquals(userId, clearInput.getUserId());
        assertEquals(recipeId, clearInput.getRecipeId());
        assertNull(clearInput.getStars());
        assertTrue(clearInput.isClearRating());
    }

    @Test
    void outputDataGettersExposeRatingAndRemovedFlag() {
        long userId = 20L;
        String recipeId = "Y999";

        UserRating rating = new UserRating(userId, recipeId, 2.0);
        RateRecipeOutputData output1 =
                new RateRecipeOutputData(rating, false);

        assertSame(rating, output1.getRating());
        assertFalse(output1.isRemoved());

        RateRecipeOutputData output2 =
                new RateRecipeOutputData(null, true);
        assertNull(output2.getRating());
        assertTrue(output2.isRemoved());
    }

    /**
     * Simple in-memory fake implementation of the data access gateway.
     */
    static class FakeRatingGateway implements UserRatingDataAccessInterface {

        private final Map<String, UserRating> storage = new HashMap<>();
        private Long lastDeletedUserId;
        private String lastDeletedRecipeId;

        @Override
        public UserRating findByUserAndRecipe(long userId, String recipeId) {
            return storage.get(key(userId, recipeId));
        }

        @Override
        public void save(UserRating rating) {
            storage.put(key(rating.getUserId(), rating.getRecipeId()), rating);
        }

        @Override
        public void deleteRating(long userId, String recipeId) {
            lastDeletedUserId = userId;
            lastDeletedRecipeId = recipeId;
            storage.remove(key(userId, recipeId));
        }

        private String key(long userId, String recipeId) {
            return userId + ":" + recipeId;
        }

        Long getLastDeletedUserId() {
            return lastDeletedUserId;
        }

        String getLastDeletedRecipeId() {
            return lastDeletedRecipeId;
        }
    }

    /**
     * Presenter that just captures the last success or failure call.
     */
    static class CapturePresenter implements RateRecipeOutputBoundary {

        RateRecipeOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(RateRecipeOutputData outputData) {
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            lastError = errorMessage;
        }
    }
}
