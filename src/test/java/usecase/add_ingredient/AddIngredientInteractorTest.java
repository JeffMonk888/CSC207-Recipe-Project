package usecase.add_ingredient;

import org.junit.jupiter.api.Test;
import usecase.common.FridgeAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AddIngredientInteractor.
 * Designed to achieve 100% line and branch coverage.
 */
class AddIngredientInteractorTest {

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
    private static class FakePresenter implements AddIngredientOutputBoundary {

        AddIngredientOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(AddIngredientOutputData outputData) {
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
        AddIngredientInteractor interactor =
                new AddIngredientInteractor(fridge, presenter);

        interactor.execute(new AddIngredientInputData(null, "milk"));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("User ID code cannot be null.", presenter.lastError);
    }

    @Test
    void execute_nullIngredient_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        FakePresenter presenter = new FakePresenter();
        AddIngredientInteractor interactor =
                new AddIngredientInteractor(fridge, presenter);

        // ingredient is null → first part of (ingredient == null || ingredient.trim().isEmpty())
        interactor.execute(new AddIngredientInputData(1L, null));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Ingredient cannot be empty.", presenter.lastError);
    }

    @Test
    void execute_emptyIngredientString_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        FakePresenter presenter = new FakePresenter();
        AddIngredientInteractor interactor =
                new AddIngredientInteractor(fridge, presenter);

        // non-null but blank → second part of (ingredient == null || ingredient.trim().isEmpty())
        interactor.execute(new AddIngredientInputData(1L, "   "));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Ingredient cannot be empty.", presenter.lastError);
    }

    @Test
    void execute_duplicateIngredient_presentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        // pre-populate with existing ingredient
        fridge.addItem(1L, "egg");

        FakePresenter presenter = new FakePresenter();
        AddIngredientInteractor interactor =
                new AddIngredientInteractor(fridge, presenter);

        interactor.execute(new AddIngredientInputData(1L, "egg"));

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Ingredient is already in your fridge.", presenter.lastError);

        // still only one copy in storage
        assertEquals(1, fridge.getItems(1L).size());
        assertTrue(fridge.getItems(1L).contains("egg"));
    }

    @Test
    void execute_newIngredient_addsToFridgeAndPresentsSuccess() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        FakePresenter presenter = new FakePresenter();
        AddIngredientInteractor interactor =
                new AddIngredientInteractor(fridge, presenter);

        interactor.execute(new AddIngredientInputData(1L, "tomato"));

        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals(1L, presenter.lastSuccess.getUserId());
        assertEquals("tomato", presenter.lastSuccess.getIngredient());

        List<String> items = fridge.getItems(1L);
        assertEquals(1, items.size());
        assertTrue(items.contains("tomato"));
    }

}
