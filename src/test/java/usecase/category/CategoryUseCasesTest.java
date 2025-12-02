package usecase.category;

import domain.entity.Category;
import domain.entity.SavedRecipe;
import org.junit.jupiter.api.Test;
import usecase.category.assign_category.AssignCategoryInputData;
import usecase.category.assign_category.AssignCategoryInteractor;
import usecase.category.assign_category.AssignCategoryOutputBoundary;
import usecase.category.assign_category.AssignCategoryOutputData;
import usecase.category.create_category.CreateCategoryInputData;
import usecase.category.create_category.CreateCategoryInteractor;
import usecase.category.create_category.CreateCategoryOutputBoundary;
import usecase.category.create_category.CreateCategoryOutputData;
import usecase.category.delete_category.DeleteCategoryInputData;
import usecase.category.delete_category.DeleteCategoryInteractor;
import usecase.category.delete_category.DeleteCategoryOutputBoundary;
import usecase.category.delete_category.DeleteCategoryOutputData;
import usecase.category.filter_by_category.FilterByCategoryInputData;
import usecase.category.filter_by_category.FilterByCategoryInteractor;
import usecase.category.filter_by_category.FilterByCategoryOutputBoundary;
import usecase.category.filter_by_category.FilterByCategoryOutputData;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInputData;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryInteractor;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryOutputBoundary;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryOutputData;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Category-related use cases (UC10).
 * Designed to achieve 100% line and branch coverage of the Category
 * interactors and their simple data classes.
 */
class CategoryUseCasesTest {

    /**
     * Simple fake implementation of CategoryDataAccessInterface for testing.
     * It exposes fields so individual tests can configure behaviour and assert
     * on which methods were called.
     */
    private static class FakeCategoryGateway implements CategoryDataAccessInterface {

        // Configurable return values
        boolean categoryNameExistsReturn;
        boolean categoryExistsForUserReturn;
        List<String> recipeIdsForCategoryReturn = new ArrayList<>();
        List<Category> categoriesForUserReturn = new ArrayList<>();
        Category categoryToCreate;

        // Tracking of calls
        Long lastCategoryNameExistsUserId;
        String lastCategoryNameExistsName;
        int categoryNameExistsCallCount;

        Long lastCreateUserId;
        String lastCreateName;
        int createCategoryCallCount;

        Long lastCategoryExistsUserId;
        Long lastCategoryExistsCategoryId;
        int categoryExistsCallCount;

        Long lastAssignUserId;
        Long lastAssignCategoryId;
        List<String> lastAssignRecipeIds;
        int assignCallCount;

        Long lastGetRecipeIdsUserId;
        Long lastGetRecipeIdsCategoryId;
        int getRecipeIdsCallCount;

        Long lastRemoveUserId;
        Long lastRemoveCategoryId;
        String lastRemoveRecipeId;
        int removeCallCount;

        Long lastDeleteUserId;
        Long lastDeleteCategoryId;
        int deleteCallCount;

        Long lastFindCategoriesUserId;
        int findCategoriesCallCount;

        @Override
        public boolean categoryNameExists(Long userId, String name) {
            categoryNameExistsCallCount++;
            lastCategoryNameExistsUserId = userId;
            lastCategoryNameExistsName = name;
            return categoryNameExistsReturn;
        }

        @Override
        public Category createCategory(Long userId, String name) {
            createCategoryCallCount++;
            lastCreateUserId = userId;
            lastCreateName = name;
            return categoryToCreate;
        }

        @Override
        public boolean categoryExistsForUser(Long userId, Long categoryId) {
            categoryExistsCallCount++;
            lastCategoryExistsUserId = userId;
            lastCategoryExistsCategoryId = categoryId;
            return categoryExistsForUserReturn;
        }

        @Override
        public List<Category> findCategoriesForUser(Long userId) {
            findCategoriesCallCount++;
            lastFindCategoriesUserId = userId;
            return new ArrayList<>(categoriesForUserReturn);
        }

        @Override
        public void assignRecipesToCategory(Long userId, Long categoryId,
                                            List<String> recipeIds) {
            assignCallCount++;
            lastAssignUserId = userId;
            lastAssignCategoryId = categoryId;
            lastAssignRecipeIds = recipeIds;
        }

        @Override
        public List<String> getRecipeIdsForCategory(Long userId, Long categoryId) {
            getRecipeIdsCallCount++;
            lastGetRecipeIdsUserId = userId;
            lastGetRecipeIdsCategoryId = categoryId;
            return recipeIdsForCategoryReturn == null
                    ? null
                    : new ArrayList<>(recipeIdsForCategoryReturn);
        }

        @Override
        public void removeRecipeFromCategory(Long userId, Long categoryId,
                                             String recipeId) {
            removeCallCount++;
            lastRemoveUserId = userId;
            lastRemoveCategoryId = categoryId;
            lastRemoveRecipeId = recipeId;
        }

        @Override
        public void deleteCategory(Long userId, Long categoryId) {
            deleteCallCount++;
            lastDeleteUserId = userId;
            lastDeleteCategoryId = categoryId;
        }
    }

    /**
     * Fake MotionForRecipe used by FilterByCategoryInteractor.
     * Only findByUserId is actually used in these tests.
     */
    private static class FakeSavedRecipeGateway implements MotionForRecipe {

        ArrayList<SavedRecipe> recipesToReturn = new ArrayList<>();

        Long lastFindUserId;
        int findCallCount;

        @Override
        public boolean exists(Long userId, String recipeKey) {
            return false;
        }

        @Override
        public void save(SavedRecipe newSave) {
            // not needed for tests
        }

        @Override
        public ArrayList<SavedRecipe> findByUserId(Long userId) {
            findCallCount++;
            lastFindUserId = userId;
            return new ArrayList<>(recipesToReturn);
        }

        @Override
        public boolean delete(Long userId, String recipeKey) {
            return false;
        }
    }

    // ---- Fake presenters for each use case ----

    private static class FakeCreatePresenter implements CreateCategoryOutputBoundary {
        CreateCategoryOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(CreateCategoryOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    private static class FakeDeletePresenter implements DeleteCategoryOutputBoundary {
        DeleteCategoryOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(DeleteCategoryOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    private static class FakeAssignPresenter implements AssignCategoryOutputBoundary {
        AssignCategoryOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(AssignCategoryOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    private static class FakeFilterPresenter implements FilterByCategoryOutputBoundary {
        FilterByCategoryOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(FilterByCategoryOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    private static class FakeRemovePresenter
            implements RemoveRecipeFromCategoryOutputBoundary {

        RemoveRecipeFromCategoryOutputData lastSuccess;
        String lastError;
        int successCount;
        int failureCount;

        @Override
        public void presentSuccess(RemoveRecipeFromCategoryOutputData outputData) {
            successCount++;
            lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            failureCount++;
            lastError = errorMessage;
        }
    }

    // ---- Tests for entity and simple data classes ----

    @Test
    void categoryEntity_gettersSettersAndToString() {
        Category category = new Category(1L, 2L, "Dessert");

        assertEquals(1L, category.getId());
        assertEquals(2L, category.getUserId());
        assertEquals("Dessert", category.getName());

        category.setId(10L);
        category.setUserId(20L);
        category.setName("Main");

        assertEquals(10L, category.getId());
        assertEquals(20L, category.getUserId());
        assertEquals("Main", category.getName());

        String text = category.toString();
        assertNotNull(text);
        assertTrue(text.contains("id="));
        assertTrue(text.contains("userId="));
        assertTrue(text.contains("name='Main'"));
    }

    @Test
    void createCategoryInputAndOutputData_storeValues() {
        CreateCategoryInputData input =
                new CreateCategoryInputData(5L, " Snacks ");
        assertEquals(5L, input.getUserId());
        assertEquals(" Snacks ", input.getName());

        Category cat = new Category(1L, 5L, "Snacks");
        CreateCategoryOutputData output =
                new CreateCategoryOutputData(cat);
        assertSame(cat, output.getCategory());
    }

    @Test
    void assignCategoryInputAndOutputData_storeValues() {
        List<String> ids = Arrays.asList("1", "2");
        AssignCategoryInputData input =
                new AssignCategoryInputData(1L, 9L, ids);

        assertEquals(1L, input.getUserId());
        assertEquals(9L, input.getCategoryId());
        assertEquals(ids, input.getRecipeIds());

        AssignCategoryOutputData output =
                new AssignCategoryOutputData(9L, ids);
        assertEquals(9L, output.getCategoryId());
        assertEquals(ids, output.getAssignedRecipeIds());
    }

    @Test
    void deleteCategoryInputAndOutputData_storeValues() {
        DeleteCategoryInputData input =
                new DeleteCategoryInputData(3L, 7L);

        assertEquals(3L, input.getUserId());
        assertEquals(7L, input.getCategoryId());

        DeleteCategoryOutputData output =
                new DeleteCategoryOutputData(7L);
        assertEquals(7L, output.getDeletedCategoryId());
    }

    @Test
    void filterByCategoryInputAndOutputData_storeValues() {
        FilterByCategoryInputData input =
                new FilterByCategoryInputData(4L, 11L);
        assertEquals(4L, input.getUserId());
        assertEquals(11L, input.getCategoryId());

        List<SavedRecipe> list = new ArrayList<>();
        list.add(new SavedRecipe(4L, "123"));
        FilterByCategoryOutputData output =
                new FilterByCategoryOutputData(list);
        assertEquals(list, output.getSavedRecipes());
    }

    @Test
    void removeRecipeFromCategoryInputAndOutputData_storeValues() {
        RemoveRecipeFromCategoryInputData input =
                new RemoveRecipeFromCategoryInputData(2L, 8L, "42");

        assertEquals(2L, input.getUserId());
        assertEquals(8L, input.getCategoryId());
        assertEquals("42", input.getRecipeId());

        RemoveRecipeFromCategoryOutputData output =
                new RemoveRecipeFromCategoryOutputData(8L, "42");
        assertEquals(8L, output.getCategoryId());
        assertEquals("42", output.getRecipeId());
    }

    // ---- Tests for CreateCategoryInteractor ----

    @Test
    void createCategory_failsWhenNameIsNullOrBlank() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        FakeCreatePresenter presenter = new FakeCreatePresenter();
        CreateCategoryInteractor interactor =
                new CreateCategoryInteractor(gateway, presenter);

        // null name becomes "" internally
        CreateCategoryInputData nullName =
                new CreateCategoryInputData(1L, null);
        interactor.execute(nullName);

        assertEquals(0, gateway.categoryNameExistsCallCount);
        assertEquals(0, gateway.createCategoryCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category name cannot be empty.", presenter.lastError);

        // blank name after trimming
        CreateCategoryInputData blankName =
                new CreateCategoryInputData(1L, "   ");
        interactor.execute(blankName);

        assertEquals(0, gateway.categoryNameExistsCallCount);
        assertEquals(0, gateway.createCategoryCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(2, presenter.failureCount);
        assertEquals("Category name cannot be empty.", presenter.lastError);
    }

    @Test
    void createCategory_failsWhenNameAlreadyExists() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryNameExistsReturn = true;

        FakeCreatePresenter presenter = new FakeCreatePresenter();
        CreateCategoryInteractor interactor =
                new CreateCategoryInteractor(gateway, presenter);

        CreateCategoryInputData input =
                new CreateCategoryInputData(1L, "Dessert");
        interactor.execute(input);

        assertEquals(1, gateway.categoryNameExistsCallCount);
        assertEquals(0, gateway.createCategoryCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category already exists.", presenter.lastError);
        assertEquals(Long.valueOf(1L), gateway.lastCategoryNameExistsUserId);
        assertEquals("Dessert", gateway.lastCategoryNameExistsName);
    }

    @Test
    void createCategory_succeedsForNewName() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        Category created = new Category(10L, 1L, "Dessert");
        gateway.categoryNameExistsReturn = false;
        gateway.categoryToCreate = created;

        FakeCreatePresenter presenter = new FakeCreatePresenter();
        CreateCategoryInteractor interactor =
                new CreateCategoryInteractor(gateway, presenter);

        CreateCategoryInputData input =
                new CreateCategoryInputData(1L, "Dessert");
        interactor.execute(input);

        assertEquals(1, gateway.categoryNameExistsCallCount);
        assertEquals(1, gateway.createCategoryCallCount);
        assertEquals(Long.valueOf(1L), gateway.lastCreateUserId);
        assertEquals("Dessert", gateway.lastCreateName);
        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertSame(created, presenter.lastSuccess.getCategory());
    }

    // ---- Tests for DeleteCategoryInteractor ----

    @Test
    void deleteCategory_failsWhenCategoryDoesNotExist() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = false;

        FakeDeletePresenter presenter = new FakeDeletePresenter();
        DeleteCategoryInteractor interactor =
                new DeleteCategoryInteractor(gateway, presenter);

        DeleteCategoryInputData input =
                new DeleteCategoryInputData(1L, 99L);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.deleteCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category not found for this user.", presenter.lastError);
    }

    @Test
    void deleteCategory_succeedsWhenCategoryExists() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;

        FakeDeletePresenter presenter = new FakeDeletePresenter();
        DeleteCategoryInteractor interactor =
                new DeleteCategoryInteractor(gateway, presenter);

        DeleteCategoryInputData input =
                new DeleteCategoryInputData(1L, 5L);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.deleteCallCount);
        assertEquals(Long.valueOf(1L), gateway.lastDeleteUserId);
        assertEquals(Long.valueOf(5L), gateway.lastDeleteCategoryId);

        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals(Long.valueOf(5L),
                presenter.lastSuccess.getDeletedCategoryId());
    }

    // ---- Tests for AssignCategoryInteractor ----

    @Test
    void assignCategory_failsWhenCategoryDoesNotExist() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = false;

        FakeAssignPresenter presenter = new FakeAssignPresenter();
        AssignCategoryInteractor interactor =
                new AssignCategoryInteractor(gateway, presenter);

        AssignCategoryInputData input =
                new AssignCategoryInputData(2L, 7L,
                        Arrays.asList("1", "2"));
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.assignCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category not found for this user.",
                presenter.lastError);
    }

    @Test
    void assignCategory_failsWhenNoRecipesProvided() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;

        FakeAssignPresenter presenter = new FakeAssignPresenter();
        AssignCategoryInteractor interactor =
                new AssignCategoryInteractor(gateway, presenter);

        // null list
        AssignCategoryInputData inputNull =
                new AssignCategoryInputData(2L, 7L, null);
        interactor.execute(inputNull);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.assignCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("No recipes selected for assignment.",
                presenter.lastError);

        // empty list
        AssignCategoryInputData inputEmpty =
                new AssignCategoryInputData(2L, 7L,
                        Collections.emptyList());
        interactor.execute(inputEmpty);

        assertEquals(2, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.assignCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(2, presenter.failureCount);
        assertEquals("No recipes selected for assignment.",
                presenter.lastError);
    }

    @Test
    void assignCategory_succeedsWhenCategoryExistsAndRecipesProvided() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;

        FakeAssignPresenter presenter = new FakeAssignPresenter();
        AssignCategoryInteractor interactor =
                new AssignCategoryInteractor(gateway, presenter);

        List<String> recipeIds = Arrays.asList("3", "4");
        AssignCategoryInputData input =
                new AssignCategoryInputData(2L, 7L, recipeIds);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.assignCallCount);
        assertEquals(Long.valueOf(2L), gateway.lastAssignUserId);
        assertEquals(Long.valueOf(7L), gateway.lastAssignCategoryId);
        assertEquals(recipeIds, gateway.lastAssignRecipeIds);

        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals(Long.valueOf(7L),
                presenter.lastSuccess.getCategoryId());
        assertEquals(recipeIds,
                presenter.lastSuccess.getAssignedRecipeIds());
    }

    // ---- Tests for RemoveRecipeFromCategoryInteractor ----

    @Test
    void removeRecipe_failsWhenIdsMissing() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        FakeRemovePresenter presenter = new FakeRemovePresenter();
        RemoveRecipeFromCategoryInteractor interactor =
                new RemoveRecipeFromCategoryInteractor(gateway, presenter);

        RemoveRecipeFromCategoryInputData input =
                new RemoveRecipeFromCategoryInputData(null, 1L, "10");
        interactor.execute(input);

        assertEquals(0, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.removeCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Missing user, category or recipe id.",
                presenter.lastError);
    }

    @Test
    void removeRecipe_failsWhenCategoryDoesNotExist() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = false;

        FakeRemovePresenter presenter = new FakeRemovePresenter();
        RemoveRecipeFromCategoryInteractor interactor =
                new RemoveRecipeFromCategoryInteractor(gateway, presenter);

        RemoveRecipeFromCategoryInputData input =
                new RemoveRecipeFromCategoryInputData(1L, 2L, "10");
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.removeCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category not found for this user.",
                presenter.lastError);
    }

    @Test
    void removeRecipe_failsWhenRecipeNotInCategory() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;
        gateway.recipeIdsForCategoryReturn = Arrays.asList("1", "2");

        FakeRemovePresenter presenter = new FakeRemovePresenter();
        RemoveRecipeFromCategoryInteractor interactor =
                new RemoveRecipeFromCategoryInteractor(gateway, presenter);

        RemoveRecipeFromCategoryInputData input =
                new RemoveRecipeFromCategoryInputData(1L, 2L, "5");
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.getRecipeIdsCallCount);
        assertEquals(0, gateway.removeCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Recipe is not currently in this category.",
                presenter.lastError);
    }

    @Test
    void removeRecipe_succeedsWhenRecipePresentInCategory() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;
        gateway.recipeIdsForCategoryReturn = Arrays.asList("10", "20");

        FakeRemovePresenter presenter = new FakeRemovePresenter();
        RemoveRecipeFromCategoryInteractor interactor =
                new RemoveRecipeFromCategoryInteractor(gateway, presenter);

        RemoveRecipeFromCategoryInputData input =
                new RemoveRecipeFromCategoryInputData(1L, 2L, "10");
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.getRecipeIdsCallCount);
        assertEquals(1, gateway.removeCallCount);
        assertEquals(Long.valueOf(1L), gateway.lastRemoveUserId);
        assertEquals(Long.valueOf(2L), gateway.lastRemoveCategoryId);
        assertEquals("10", gateway.lastRemoveRecipeId);

        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertEquals(Long.valueOf(2L),
                presenter.lastSuccess.getCategoryId());
        assertEquals("10", presenter.lastSuccess.getRecipeId());
    }

    // ---- Tests for FilterByCategoryInteractor ----

    @Test
    void filterByCategory_failsWhenCategoryDoesNotExist() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = false;

        FakeSavedRecipeGateway savedGateway = new FakeSavedRecipeGateway();
        FakeFilterPresenter presenter = new FakeFilterPresenter();
        FilterByCategoryInteractor interactor =
                new FilterByCategoryInteractor(gateway, savedGateway, presenter);

        FilterByCategoryInputData input =
                new FilterByCategoryInputData(1L, 3L);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(0, gateway.getRecipeIdsCallCount);
        assertEquals(0, savedGateway.findCallCount);
        assertEquals(0, presenter.successCount);
        assertEquals(1, presenter.failureCount);
        assertEquals("Category not found for this user.",
                presenter.lastError);
    }

    @Test
    void filterByCategory_succeedsWithEmptyResultWhenNoRecipesAssigned() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;
        gateway.recipeIdsForCategoryReturn = Collections.emptyList();

        FakeSavedRecipeGateway savedGateway = new FakeSavedRecipeGateway();
        FakeFilterPresenter presenter = new FakeFilterPresenter();
        FilterByCategoryInteractor interactor =
                new FilterByCategoryInteractor(gateway, savedGateway, presenter);

        FilterByCategoryInputData input =
                new FilterByCategoryInputData(1L, 3L);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.getRecipeIdsCallCount);
        assertEquals(0, savedGateway.findCallCount);
        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);
        assertTrue(presenter.lastSuccess.getSavedRecipes().isEmpty());
    }

    @Test
    void filterByCategory_succeedsAndFiltersCorrectly() {
        FakeCategoryGateway gateway = new FakeCategoryGateway();
        gateway.categoryExistsForUserReturn = true;
        gateway.recipeIdsForCategoryReturn = Arrays.asList("10", "20");

        FakeSavedRecipeGateway savedGateway = new FakeSavedRecipeGateway();
        SavedRecipe sr1 = new SavedRecipe(1L, "10");
        SavedRecipe sr2 = new SavedRecipe(1L, "20");
        SavedRecipe sr3 = new SavedRecipe(1L, "30");
        savedGateway.recipesToReturn.add(sr1);
        savedGateway.recipesToReturn.add(sr2);
        savedGateway.recipesToReturn.add(sr3);

        FakeFilterPresenter presenter = new FakeFilterPresenter();
        FilterByCategoryInteractor interactor =
                new FilterByCategoryInteractor(gateway, savedGateway, presenter);

        FilterByCategoryInputData input =
                new FilterByCategoryInputData(1L, 3L);
        interactor.execute(input);

        assertEquals(1, gateway.categoryExistsCallCount);
        assertEquals(1, gateway.getRecipeIdsCallCount);
        assertEquals(1, savedGateway.findCallCount);
        assertEquals(Long.valueOf(1L), savedGateway.lastFindUserId);

        assertEquals(1, presenter.successCount);
        assertEquals(0, presenter.failureCount);
        assertNotNull(presenter.lastSuccess);

        List<SavedRecipe> filtered =
                presenter.lastSuccess.getSavedRecipes();
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(sr1));
        assertTrue(filtered.contains(sr2));
        assertFalse(filtered.contains(sr3));
    }
}
