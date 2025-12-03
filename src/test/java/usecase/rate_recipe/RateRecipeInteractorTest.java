package usecase.rate_recipe;

import domain.entity.UserRating;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RateRecipeInteractor.
 * Aims for 100% line and branch coverage of the rate_recipe use case.
 */
class RateRecipeInteractorTest {

    /**
     * Simple fake data access implementation for UserRatingDataAccessInterface.
     * Records method calls so tests can assert behaviour.
     */
    private static class FakeRatingDataAccess implements UserRatingDataAccessInterface {

        long lastFindUserId;
        String lastFindRecipeId;
        int findCallCount = 0;

        long lastDeleteUserId;
        String lastDeleteRecipeId;
        int deleteCallCount = 0;

        UserRating lastSavedRating;
        int saveCallCount = 0;

        // Rating that will be returned from findByUserAndRecipe.
        UserRating ratingToReturn;

        @Override
        public UserRating findByUserAndRecipe(long userId, String recipeId) {
            findCallCount++;
            lastFindUserId = userId;
            lastFindRecipeId = recipeId;
            return ratingToReturn;
        }

        @Override
        public void save(UserRating rating) {
            saveCallCount++;
            lastSavedRating = rating;
        }

        @Override
        public void deleteRating(long userId, String recipeId) {
            deleteCallCount++;
            lastDeleteUserId = userId;
            lastDeleteRecipeId = recipeId;
        }
    }

    /**
     * Fake presenter for RateRecipeOutputBoundary.
     */
    private static class FakePresenter implements RateRecipeOutputBoundary {

        RateRecipeOutputData lastSuccess;
        String lastFailure;

        int successCount = 0;
        int failureCount = 0;

        @Override
        public void presentSuccess(RateRecipeOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastFailure = errorMessage;
        }
    }

    // ---------- tests for RateRecipeInputData factories ----------

    @Test
    void forRating_factorySetsFieldsCorrectly() {
        long userId = 10L;
        String recipeId = "recipe-123";
        double stars = 3.5;

        RateRecipeInputData input = RateRecipeInputData.forRating(userId, recipeId, stars);

        assertEquals(userId, input.getUserId());
        assertEquals(recipeId, input.getRecipeId());
        assertEquals(stars, input.getStars());
        assertFalse(input.isClearRating());
    }

    @Test
    void forClear_factorySetsClearFlagAndNullStars() {
        long userId = 20L;
        String recipeId = "recipe-clear";

        RateRecipeInputData input = RateRecipeInputData.forClear(userId, recipeId);

        assertEquals(userId, input.getUserId());
        assertEquals(recipeId, input.getRecipeId());
        assertNull(input.getStars());
        assertTrue(input.isClearRating());
    }

    // ---------- tests for RateRecipeInteractor behaviour ----------

    @Test
    void execute_clearsRating_whenClearRatingIsTrue() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        long userId = 1L;
        String recipeId = "abc123";
        RateRecipeInputData input = RateRecipeInputData.forClear(userId, recipeId);

        interactor.execute(input);

        // Data access: deleteRating should be called, others not.
        assertEquals(1, dataAccess.deleteCallCount);
        assertEquals(userId, dataAccess.lastDeleteUserId);
        assertEquals(recipeId, dataAccess.lastDeleteRecipeId);
        assertEquals(0, dataAccess.findCallCount);
        assertEquals(0, dataAccess.saveCallCount);

        // Presenter: success with removed = true and rating = null.
        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertTrue(presenter.lastSuccess.isRemoved());
        assertNull(presenter.lastSuccess.getRating());
    }

    @Test
    void execute_fails_whenStarsNullAndNotClearing() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        // stars == null and clearRating == false
        RateRecipeInputData input =
                new RateRecipeInputData(2L, "recipe-null-stars", null, false);

        interactor.execute(input);

        // Presenter should report failure with correct message.
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Stars cannot be null unless clearRating is true.",
                presenter.lastFailure);

        // Data access should not be used.
        assertEquals(0, dataAccess.findCallCount);
        assertEquals(0, dataAccess.saveCallCount);
        assertEquals(0, dataAccess.deleteCallCount);
    }

    @Test
    void execute_fails_whenStarsBelowZero() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        // stars < 0.0 -> invalid
        RateRecipeInputData input =
                RateRecipeInputData.forRating(3L, "recipe-negative", -0.5);

        interactor.execute(input);

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Rating must be between 0.0 and 5.0 in steps of 0.5.",
                presenter.lastFailure);

        assertEquals(0, dataAccess.findCallCount);
        assertEquals(0, dataAccess.saveCallCount);
        assertEquals(0, dataAccess.deleteCallCount);
    }

    @Test
    void execute_fails_whenStarsAboveMax() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        // stars > 5.0 -> invalid, also exercises the same upper-bound branch
        RateRecipeInputData input =
                RateRecipeInputData.forRating(30L, "recipe-too-high", 5.5);

        interactor.execute(input);

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Rating must be between 0.0 and 5.0 in steps of 0.5.",
                presenter.lastFailure);

        assertEquals(0, dataAccess.findCallCount);
        assertEquals(0, dataAccess.saveCallCount);
        assertEquals(0, dataAccess.deleteCallCount);
    }

    @Test
    void execute_fails_whenStarsNotMultipleOfHalf() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        // Stars in range but not multiple of 0.5 -> invalid (exercises scaled logic)
        RateRecipeInputData input =
                RateRecipeInputData.forRating(4L, "recipe-step-invalid", 4.2);

        interactor.execute(input);

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Rating must be between 0.0 and 5.0 in steps of 0.5.",
                presenter.lastFailure);

        assertEquals(0, dataAccess.findCallCount);
        assertEquals(0, dataAccess.saveCallCount);
        assertEquals(0, dataAccess.deleteCallCount);
    }

    @Test
    void execute_createsNewRating_whenNoExistingRating() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        // ratingToReturn is null by default -> no existing rating
        FakePresenter presenter = new FakePresenter();
        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        long userId = 5L;
        String recipeId = "recipe-new";
        double stars = 4.5;

        RateRecipeInputData input =
                RateRecipeInputData.forRating(userId, recipeId, stars);

        interactor.execute(input);

        // Data access: find + save should be called once.
        assertEquals(1, dataAccess.findCallCount);
        assertEquals(userId, dataAccess.lastFindUserId);
        assertEquals(recipeId, dataAccess.lastFindRecipeId);

        assertEquals(1, dataAccess.saveCallCount);
        assertNotNull(dataAccess.lastSavedRating);

        UserRating saved = dataAccess.lastSavedRating;
        assertEquals(userId, saved.getUserId());
        assertEquals(recipeId, saved.getRecipeId());
        assertEquals(stars, saved.getStars());
        assertNull(saved.getId());
        assertNotNull(saved.getUpdatedAt());

        // Presenter: success, removed = false, rating is the saved one.
        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());
        assertSame(saved, presenter.lastSuccess.getRating());
    }

    @Test
    void execute_updatesExistingRating_whenRatingAlreadyExists() {
        FakeRatingDataAccess dataAccess = new FakeRatingDataAccess();
        FakePresenter presenter = new FakePresenter();

        long userId = 6L;
        String recipeId = "recipe-existing";

        Instant oldInstant = Instant.ofEpochSecond(0);
        UserRating existing = new UserRating(
                100L,
                userId,
                recipeId,
                2.0,
                oldInstant
        );
        dataAccess.ratingToReturn = existing;

        RateRecipeInteractor interactor = new RateRecipeInteractor(dataAccess, presenter);

        double newStars = 5.0;
        RateRecipeInputData input =
                RateRecipeInputData.forRating(userId, recipeId, newStars);

        interactor.execute(input);

        // Data access: find + save, no delete.
        assertEquals(1, dataAccess.findCallCount);
        assertEquals(1, dataAccess.saveCallCount);
        assertEquals(0, dataAccess.deleteCallCount);

        // The existing object should be updated and saved.
        assertSame(existing, dataAccess.lastSavedRating);
        assertEquals(newStars, existing.getStars());
        assertNotNull(existing.getUpdatedAt());
        assertNotEquals(oldInstant, existing.getUpdatedAt());

        // Presenter: success with removed = false and updated rating.
        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());
        assertSame(existing, presenter.lastSuccess.getRating());
    }

    @Test
    void rateRecipeOutputData_gettersReturnConstructorValues() {
        UserRating rating = new UserRating(7L, "recipe-output", 3.0);
        RateRecipeOutputData output = new RateRecipeOutputData(rating, true);

        assertSame(rating, output.getRating());
        assertTrue(output.isRemoved());
    }
}
