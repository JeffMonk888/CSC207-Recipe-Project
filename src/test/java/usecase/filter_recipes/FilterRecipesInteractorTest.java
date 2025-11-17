package usecase.filter_recipes;

import domain.entity.NutritionInfo;
import domain.entity.Recipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterRecipesInteractorTest {

    @Test
    void filtersByMaxCaloriesAndSortsByCaloriesAsc() {
        // --- Arrange ---
        Recipe r1 = recipeWithCaloriesAndTime(1L, "A", 500.0, 30);
        Recipe r2 = recipeWithCaloriesAndTime(2L, "B", 300.0, 15);
        Recipe r3 = recipeWithCaloriesAndTime(3L, "C", 800.0, 45);

        List<Recipe> inputList = List.of(r1, r2, r3);

        Double maxCalories = 600.0;  // should remove r3 (800 kcal)

        CapturePresenter presenter = new CapturePresenter();
        FilterRecipesInteractor interactor = new FilterRecipesInteractor(presenter);

        FilterRecipesInputData inputData = new FilterRecipesInputData(
                inputList,
                maxCalories,
                FilterRecipesInputData.SortBy.CALORIES,
                FilterRecipesInputData.SortOrder.ASC
        );

        // --- Act ---
        interactor.execute(inputData);

        // --- Assert ---
        assertNotNull(presenter.lastOutput, "Presenter should be called");
        List<Recipe> results = presenter.lastOutput.getResults();

        // r3 (800 kcal) should be filtered out
        assertEquals(2, results.size(), "Only 2 recipes should remain");

        // sorted ASC by calories: r2 (300) then r1 (500)
        assertEquals("B", results.get(0).getTitle());
        assertEquals("A", results.get(1).getTitle());
    }

    @Test
    void sortsByReadyTimeDescWhenNoCalorieFilter() {
        // --- Arrange ---
        Recipe r1 = recipeWithCaloriesAndTime(1L, "Fast", 400.0, 10);
        Recipe r2 = recipeWithCaloriesAndTime(2L, "Medium", 400.0, 20);
        Recipe r3 = recipeWithCaloriesAndTime(3L, "Slow", 400.0, 60);

        List<Recipe> inputList = List.of(r1, r2, r3);

        CapturePresenter presenter = new CapturePresenter();
        FilterRecipesInteractor interactor = new FilterRecipesInteractor(presenter);

        // no maxCalories filter => null
        FilterRecipesInputData inputData = new FilterRecipesInputData(
                inputList,
                null, // no filter
                FilterRecipesInputData.SortBy.READY_TIME,
                FilterRecipesInputData.SortOrder.DESC
        );

        // --- Act ---
        interactor.execute(inputData);

        // --- Assert ---
        assertNotNull(presenter.lastOutput);
        List<Recipe> results = presenter.lastOutput.getResults();
        assertEquals(3, results.size());

        // DESC by ready time: Slow (60) -> Medium (20) -> Fast (10)
        assertEquals("Slow", results.get(0).getTitle());
        assertEquals("Medium", results.get(1).getTitle());
        assertEquals("Fast", results.get(2).getTitle());
    }

    // ---------- helpers & fakes ----------

    /** Build a Recipe with given calories and prep time, other fields minimal. */
    private static Recipe recipeWithCaloriesAndTime(Long id, String title,
                                                    Double calories, Integer readyMinutes) {
        NutritionInfo nutrition = new NutritionInfo(
                null,          // id
                calories,      // calories
                null,          // protein
                null,          // fat
                null           // carbs
        );

        // matches your 10-arg constructor:
        return new Recipe(
                id,
                title,
                "",            // description
                1,             // servings
                readyMinutes,  // prepTimeInMinutes
                "Test Source",
                "http://example.com",
                null,          // image
                String.valueOf(id),
                nutrition
        );
    }

    /** Simple presenter that just remembers the last output. */
    static class CapturePresenter implements FilterRecipesOutputBoundary {
        FilterRecipesOutputData lastOutput;

        @Override
        public void present(FilterRecipesOutputData outputData) {
            this.lastOutput = outputData;
        }
    }
}
