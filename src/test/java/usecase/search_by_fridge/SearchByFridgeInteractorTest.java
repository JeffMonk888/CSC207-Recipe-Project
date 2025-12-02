package usecase.search_by_fridge;

import domain.entity.RecipePreview;
import org.junit.jupiter.api.Test;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchByFridgeInteractor.
 * Designed to achieve 100% line coverage.
 */
class SearchByFridgeInteractorTest {

    /** In-memory FridgeAccess double. */
    private static class FakeFridgeAccess implements FridgeAccess {

        private final Map<Long, List<String>> storage = new HashMap<>();

        void setItems(Long userId, List<String> items) {
            storage.put(userId, new ArrayList<>(items));
        }

        @Override
        public boolean hasItem(Long userId, String item) {
            List<String> items = storage.getOrDefault(userId, Collections.emptyList());
            return items.contains(item);
        }

        @Override
        public void addItem(Long userId, String item) {
            storage.computeIfAbsent(userId, id -> new ArrayList<>()).add(item);
        }

        @Override
        public boolean removeItem(Long userId, String item) {
            List<String> items = storage.getOrDefault(userId, Collections.emptyList());
            return items.remove(item);
        }

        @Override
        public List<String> getItems(Long userId) {
            return new ArrayList<>(storage.getOrDefault(userId, Collections.emptyList()));
        }
    }

    /** In-memory RecipeByIngredientsAccess double. */
    private static class FakeRecipeByIngredientsAccess implements RecipeByIngredientsAccess {

        List<RecipePreview> recipesToReturn = new ArrayList<>();
        boolean shouldThrow = false;
        String exceptionMessage = "boom";

        @Override
        public List<RecipePreview> getRecipesForIngredients(
                List<String> ingredientList, int number, int offset) throws Exception {
            if (shouldThrow) {
                throw new Exception(exceptionMessage);
            }
            return recipesToReturn;
        }
    }

    /** Presenter double that records calls. */
    private static class FakePresenter implements SearchByFridgeOutputBoundary {

        SearchByFridgeOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(SearchByFridgeOutputData outputData) {
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
        FakeRecipeByIngredientsAccess recipeAccess = new FakeRecipeByIngredientsAccess();
        FakePresenter presenter = new FakePresenter();

        SearchByFridgeInteractor interactor =
                new SearchByFridgeInteractor(fridge, recipeAccess, presenter);

        SearchByFridgeInputData input =
                new SearchByFridgeInputData(null, 10, 0);

        interactor.execute(input);

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("User ID cannot be null.", presenter.lastError);
    }

    @Test
    void execute_emptyFridge_setsError_callsApi_andEndsWithFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        // no items -> empty fridge

        FakeRecipeByIngredientsAccess recipeAccess = new FakeRecipeByIngredientsAccess();
        recipeAccess.recipesToReturn = Collections.emptyList();

        FakePresenter presenter = new FakePresenter();

        SearchByFridgeInteractor interactor =
                new SearchByFridgeInteractor(fridge, recipeAccess, presenter);

        SearchByFridgeInputData input =
                new SearchByFridgeInputData(1L, 5, 0);

        interactor.execute(input);

        // error set due to empty fridge -> final failure
        assertEquals(1, presenter.failureCount);
        assertEquals("Your fridge is empty. Please add some ingredients first.", presenter.lastError);

        // try-block still builds outputData and calls presentSuccess once
        assertEquals(1, presenter.successCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals(0, presenter.lastSuccess.getRecipes().size());
        assertEquals(0, presenter.lastSuccess.getNextOffset());
        assertFalse(presenter.lastSuccess.hasMore());
    }

    @Test
    void execute_nonEmptyFridge_success_filtersRecipes_andCallsSuccessTwice() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        fridge.setItems(1L, Arrays.asList("egg", "milk"));

        FakeRecipeByIngredientsAccess recipeAccess = new FakeRecipeByIngredientsAccess();

        // one recipe fully covered by fridge, one missing ingredients
        RecipePreview r1 = new RecipePreview();
        r1.id = 1L;
        r1.title = "Perfect Omelette";
        r1.missedIngredientCount = 0;

        RecipePreview r2 = new RecipePreview();
        r2.id = 2L;
        r2.title = "Needs Shopping";
        r2.missedIngredientCount = 2;

        recipeAccess.recipesToReturn = Arrays.asList(r1, r2);

        FakePresenter presenter = new FakePresenter();

        SearchByFridgeInteractor interactor =
                new SearchByFridgeInteractor(fridge, recipeAccess, presenter);

        SearchByFridgeInputData input =
                new SearchByFridgeInputData(1L, 10, 3);

        interactor.execute(input);

        // success path: once inside try, once at the end
        assertEquals(2, presenter.successCount);
        assertEquals(0, presenter.failureCount);

        SearchByFridgeOutputData out = presenter.lastSuccess;
        assertNotNull(out);

        // only the fully-covered recipe should remain
        List<RecipePreview> filtered = out.getRecipes();
        assertEquals(1, filtered.size());
        assertEquals("Perfect Omelette", filtered.get(0).title);

        // nextOffset = offset + apiResults.size() = 3 + 2
        assertEquals(5, out.getNextOffset());
        // hasMore = apiResults.size() == number -> 2 == 10 -> false
        assertFalse(out.hasMore());
    }

    @Test
    void execute_apiThrows_setsError_andPresentsFailure() {
        FakeFridgeAccess fridge = new FakeFridgeAccess();
        fridge.setItems(1L, Collections.singletonList("egg"));

        FakeRecipeByIngredientsAccess recipeAccess = new FakeRecipeByIngredientsAccess();
        recipeAccess.shouldThrow = true;
        recipeAccess.exceptionMessage = "Network down";

        FakePresenter presenter = new FakePresenter();

        SearchByFridgeInteractor interactor =
                new SearchByFridgeInteractor(fridge, recipeAccess, presenter);

        SearchByFridgeInputData input =
                new SearchByFridgeInputData(1L, 5, 0);

        interactor.execute(input);

        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Failed to fetch recipes: Network down", presenter.lastError);
    }
}
