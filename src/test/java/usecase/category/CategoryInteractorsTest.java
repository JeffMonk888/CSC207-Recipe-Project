package usecase.category;

import data.category.InMemoryCategoryGateway;
import domain.entity.Category;
import domain.entity.SavedRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.category.create_category.CreateCategoryInteractor;
import usecase.category.create_category.CreateCategoryInputData;
import usecase.category.create_category.CreateCategoryOutputBoundary;
import usecase.category.create_category.CreateCategoryOutputData;
import usecase.category.filter_by_category.FilterByCategoryInputData;
import usecase.category.filter_by_category.FilterByCategoryOutputBoundary;
import usecase.category.filter_by_category.FilterByCategoryInteractor;
import usecase.category.filter_by_category.FilterByCategoryOutputData;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC10: Create/Customise Category for filter.
 *
 * Covers:
 *  - Create category success & duplicate name failure
 *  - Filtering by category should return only SavedRecipe
 *    belonging to the user AND assigned to that category
 */
class CategoryInteractorsTest {

    private InMemoryCategoryGateway categoryGateway;
    private MotionForRecipe savedGateway;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        categoryGateway = new InMemoryCategoryGateway();
        savedGateway = new FakeSavedGateway();
    }

    @Test
    void createCategorySuccessAndDuplicateNameFails() {
        CaptureCreatePresenter presenter = new CaptureCreatePresenter();
        CreateCategoryInteractor interactor =
                new CreateCategoryInteractor(categoryGateway, presenter);

        // First execution: success
        interactor.execute(new CreateCategoryInputData(userId, "Quick Meals"));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        Category cat = presenter.lastSuccess.getCategory();
        assertEquals("Quick Meals", cat.getName());
        assertEquals(userId, cat.getUserId());

        // Second execution: duplicate name -> failure
        presenter.reset();
        interactor.execute(new CreateCategoryInputData(userId, "Quick Meals"));

        assertNull(presenter.lastSuccess);
        assertNotNull(presenter.lastError);
    }

    @Test
    void filterByCategoryReturnsOnlyAssignedSavedRecipes() {
        // 1. Prepare saved recipes
        SavedRecipe sr1 = new SavedRecipe(userId, 101L);
        SavedRecipe sr2 = new SavedRecipe(userId, 102L);
        SavedRecipe sr3 = new SavedRecipe(2L, 999L); // from another user

        savedGateway.save(sr1);
        savedGateway.save(sr2);
        savedGateway.save(sr3);

        // 2. Create category and assign recipe 101
        Category cat = categoryGateway.createCategory(userId, "Quick Meals");
        categoryGateway.assignRecipesToCategory(userId, cat.getId(), List.of(101L));

        // 3. Filter by category
        CaptureFilterPresenter presenter = new CaptureFilterPresenter();
        FilterByCategoryInteractor interactor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, presenter);

        interactor.execute(new FilterByCategoryInputData(userId, cat.getId()));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastSuccess);

        List<SavedRecipe> results = presenter.lastSuccess.getSavedRecipes();
        assertEquals(1, results.size());
        assertEquals(101L, results.get(0).getRecipeId());
        assertEquals(userId, results.get(0).getUserId());
    }

    // ---------- Fake gateway for SavedRecipe ----------

    /**
     * Lightweight in-memory SavedRecipe gateway implementing MotionForRecipe.
     * No real persistence needed for testing.
     */
    static class FakeSavedGateway implements MotionForRecipe {
        private final List<SavedRecipe> list = new ArrayList<>();
        private final AtomicLong idCounter = new AtomicLong(1L);

        @Override
        public boolean exists(Long userId, Long recipeId) {
            return list.stream().anyMatch(s ->
                    s.getUserId().equals(userId) && s.getRecipeId().equals(recipeId));
        }

        @Override
        public void save(SavedRecipe newSave) {
            if (newSave.getId() == null) {
                newSave.setId(idCounter.getAndIncrement());
            }
            list.add(newSave);
        }

        @Override
        public ArrayList<SavedRecipe> findByUserId(Long userId) {
            ArrayList<SavedRecipe> res = new ArrayList<>();
            for (SavedRecipe s : list) {
                if (s.getUserId().equals(userId)) {
                    res.add(s);
                }
            }
            return res;
        }

        @Override
        public boolean delete(Long userId, Long recipeId) {
            return list.removeIf(s ->
                    s.getUserId().equals(userId) && s.getRecipeId().equals(recipeId));
        }
    }

    // ---------- presenters ----------

    static class CaptureCreatePresenter implements CreateCategoryOutputBoundary {
        CreateCategoryOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(CreateCategoryOutputData outputData) {
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

    static class CaptureFilterPresenter implements FilterByCategoryOutputBoundary {
        FilterByCategoryOutputData lastSuccess;
        String lastError;

        @Override
        public void presentSuccess(FilterByCategoryOutputData outputData) {
            this.lastSuccess = outputData;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.lastError = errorMessage;
        }
    }
}