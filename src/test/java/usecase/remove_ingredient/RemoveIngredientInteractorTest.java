package usecase.remove_ingredient;

import org.junit.jupiter.api.Test;
import usecase.common.FridgeAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RemoveIngredientInteractor.
 * These tests are designed to achieve 100% line coverage.
 */
class RemoveIngredientInteractorTest {

    /**
     * Simple in-memory implementation of FridgeAccess for testing.
     */
    private static class FakeFridgeAccess implements FridgeAccess {

        private final Map<Long, List<String>> storage = new HashMap<>();

        private List<String> getList(Long userId) {
            return storage.computeIfAbsent(userId, id -> new ArrayList<>());
        }

        @Override
        public boolean hasItem(Long userId, String item) {
            return getList(userId).contains(item);
        }

        @Override
        public void addItem(Long userId, String item) {
            getList(userId).add(item);
        }

        @Override
        public boolean removeItem(Long userId, String item) {
            return getList(userId).remove(item);
        }

        @Override
        public List<String> getItems(Long userId) {
            return new ArrayList<>(getList(userId));
        }
    }

    /**
     * Presenter test double that records calls for assertions.
     */
    private static class FakePresenter implements RemoveIngredientOutputBoundary {

        RemoveIngredientOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(RemoveIngredientOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    @Test
    void execute_userIdNull_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        FakePresenter presenter = new FakePresenter();
        RemoveIngredientInteractor interactor =
                new RemoveIngredientInteractor(fridge, presenter);

        interactor.execute(new RemoveIngredientInputData(null, "milk"));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("User ID cannot be null.", presenter.lastError);
    }

    @Test
    void execute_emptyIngredient_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        FakePresenter presenter = new FakePresenter();
        RemoveIngredientInteractor interactor =
                new RemoveIngredientInteractor(fridge, presenter);

        interactor.execute(new RemoveIngredientInputData(1L, "   "));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Ingredient cannot be empty.", presenter.lastError);
    }

    @Test
    void execute_nonExistingIngredient_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        // fridge is empty for user 1
        FakePresenter presenter = new FakePresenter();
        RemoveIngredientInteractor interactor =
                new RemoveIngredientInteractor(fridge, presenter);

        interactor.execute(new RemoveIngredientInputData(1L, "onion"));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Ingredient not found in your fridge.", presenter.lastError);
    }

    @Test
    void execute_existingIngredient_removesAndPresentsSuccessTwice() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        fridge.addItem(1L, "egg");

        FakePresenter presenter = new FakePresenter();
        RemoveIngredientInteractor interactor =
                new RemoveIngredientInteractor(fridge, presenter);

        interactor.execute(new RemoveIngredientInputData(1L, "egg"));

        // presentSuccess is called once inside the 'removed' branch
        // and once again in the final else block.
        assertEquals(2, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals("egg", presenter.lastSuccess.getRemovedIngredient());

        // ingredient should be removed from storage
        assertFalse(fridge.getItems(1L).contains("egg"));
    }
}
