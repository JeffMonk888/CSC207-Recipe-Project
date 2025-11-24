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
 * Assumes the UC9 design:
 *  - stars are integers in range 1..5
 *  - UserRatingDataAccessInterface has findByUserAndRecipe / save / deleteRating
 *  - RateRecipeOutputData contains only one UserRating rating field (may be null)
 *  - RateRecipeOutputBoundary has presentSuccess / presentFailure methods
 */
class RateRecipeInteractorTest {

    @Test
    void invalidRatingCausesFailureAndNoSave() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();

        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        // 0 is an invalid rating (expect 1..5)
        RateRecipeInputData input =
                new RateRecipeInputData(1L, 100L, 0);

        interactor.execute(input);

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);

        // The gateway should not contain any rating
        assertTrue(gateway.store.isEmpty());
    }

    @Test
    void createsNewRatingWhenNoneExists() {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();

        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        long userId = 1L;
        long recipeId = 100L;
        int stars = 4;

        RateRecipeInputData input =
                new RateRecipeInputData(userId, recipeId, stars);

        interactor.execute(input);

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        UserRating fromOutput = presenter.lastSuccess.getRating();
        assertEquals(userId, fromOutput.getUserId());
        assertEquals(recipeId, fromOutput.getRecipeId());
        assertEquals(stars, fromOutput.getStars());

        // Should also be saved in the gateway
        UserRating fromGateway = gateway.findByUserAndRecipe(userId, recipeId);
        assertNotNull(fromGateway);
        assertEquals(stars, fromGateway.getStars());
    }

    @Test
    void updatesExistingRating() throws InterruptedException {
        FakeRatingGateway gateway = new FakeRatingGateway();
        CapturePresenter presenter = new CapturePresenter();

        long userId = 1L;
        long recipeId = 200L;

        // Put an existing rating first
        UserRating existing = new UserRating(
                10L,               // id
                userId,
                recipeId,
                2,                 // stars
                Instant.now()
        );
        gateway.save(existing);

        // Record the "old" update time (must store before execute)
        Instant oldUpdatedAt = existing.getUpdatedAt();

        // Wait a little so updatedAt becomes slightly later
        Thread.sleep(5);

        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        RateRecipeInputData input =
                new RateRecipeInputData(userId, recipeId, 5);

        interactor.execute(input);

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        UserRating updated = presenter.lastSuccess.getRating();

        // id stays the same, only stars and updatedAt are changed
        assertEquals(10L, updated.getId());
        assertEquals(5, updated.getStars());

        // Now compare "new time vs old time", not the same object
        assertTrue(updated.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    // ---------- fake gateway & presenter ----------

    /**
     * Simple in-memory implementation for observing saved values.
     */
    static class FakeRatingGateway implements UserRatingDataAccessInterface {

        // key: "userId:recipeId"
        final Map<String, UserRating> store = new HashMap<>();

        private String key(long userId, long recipeId) {
            return userId + ":" + recipeId;
        }

        @Override
        public UserRating findByUserAndRecipe(long userId, long recipeId) {
            return store.get(key(userId, recipeId));
        }

        @Override
        public void save(UserRating rating) {
            store.put(key(rating.getUserId(), rating.getRecipeId()), rating);
        }

        @Override
        public void deleteRating(long userId, long recipeId) {
            store.remove(key(userId, recipeId));
        }
    }

    /**
     * Presenter that simply stores the last success/failure result.
     */
    static class CapturePresenter implements RateRecipeOutputBoundary {
        RateRecipeOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(RateRecipeOutputData outputData) {
            this.lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.lastError = errorMessage;
        }
    }
}