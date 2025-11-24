package usecase.rate_recipe;

import data.rating.InMemoryUserRatingGateway;
import domain.entity.UserRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateRecipeInteractorTest {

    @Test
    void createsNewRatingWithHalfStars() {
        InMemoryUserRatingGateway gateway = new InMemoryUserRatingGateway();
        CapturePresenter presenter = new CapturePresenter();

        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        interactor.execute(new RateRecipeInputData(1L, 100L, 4.5));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());

        UserRating rating = presenter.lastSuccess.getRating();
        assertEquals(4.5, rating.getStars(), 1e-9);
        assertEquals(1L, rating.getUserId());
        assertEquals(100L, rating.getRecipeId());
    }

    @Test
    void updatesExistingRating() {
        InMemoryUserRatingGateway gateway = new InMemoryUserRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        // initial rating 2.0
        interactor.execute(new RateRecipeInputData(1L, 100L, 2.0));
        UserRating first = presenter.lastSuccess.getRating();

        // update to 5.0
        interactor.execute(new RateRecipeInputData(1L, 100L, 5.0));
        UserRating updated = presenter.lastSuccess.getRating();

        assertEquals(5.0, updated.getStars(), 1e-9);
        assertEquals(first.getUserId(), updated.getUserId());
        assertEquals(first.getRecipeId(), updated.getRecipeId());
    }

    @Test
    void rejectsInvalidRating() {
        InMemoryUserRatingGateway gateway = new InMemoryUserRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        interactor.execute(new RateRecipeInputData(1L, 100L, 4.3)); // invalid (not multiple of 0.5)

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void removesExistingRatingWhenZero() {
        InMemoryUserRatingGateway gateway = new InMemoryUserRatingGateway();
        CapturePresenter presenter = new CapturePresenter();
        RateRecipeInteractor interactor =
                new RateRecipeInteractor(gateway, presenter);

        interactor.execute(new RateRecipeInputData(1L, 100L, 3.0));
        assertNotNull(presenter.lastSuccess);
        assertFalse(presenter.lastSuccess.isRemoved());

        // now remove
        presenter.reset();
        interactor.execute(new RateRecipeInputData(1L, 100L, 0.0));

        assertNull(presenter.lastError);
        assertTrue(presenter.lastSuccess.isRemoved());
        assertNull(gateway.findByUserAndRecipe(1L, 100L));
    }

    // ---- helper presenter ----
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

        void reset() {
            lastSuccess = null;
            lastError = null;
        }
    }
}
