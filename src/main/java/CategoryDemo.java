import data.category.InMemoryCategoryGateway;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.SavedRecipe;
import usecase.category.assign_category.*;
import usecase.category.create_category.*;
import usecase.category.filter_by_category.*;
import usecase.common.MotionForRecipe;

import java.util.Arrays;

public class CategoryDemo {

    public static void main(String[] args) {
        Long userId = 1L;

        // --- Gateways ---
        InMemoryCategoryGateway categoryGateway = new InMemoryCategoryGateway();

        // Use CSV-backed implementation for saved recipes
        MotionForRecipe savedGateway =
                new UserSavedRecipeAccessObject("saved_recipes_demo.csv");

        // Pretend the user already saved three recipes
        savedGateway.save(new SavedRecipe(userId, "101"));
        savedGateway.save(new SavedRecipe(userId, "102"));
        savedGateway.save(new SavedRecipe(userId, "103"));

        // --- 1. Create category ---
        CreateCategoryOutputBoundary createPresenter = new CreateCategoryOutputBoundary() {
            @Override
            public void presentSuccess(CreateCategoryOutputData outputData) {
                System.out.println("Created category: " + outputData.getCategory());
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Create category failed: " + errorMessage);
            }
        };

        CreateCategoryInputBoundary createInteractor =
                new CreateCategoryInteractor(categoryGateway, createPresenter);

        createInteractor.execute(new CreateCategoryInputData(userId, "Quick Meals"));

        Long categoryId = categoryGateway
                .findCategoriesForUser(userId)
                .get(0)
                .getId();

        // --- 2. Assign recipes to category ---
        AssignCategoryOutputBoundary assignPresenter = new AssignCategoryOutputBoundary() {
            @Override
            public void presentSuccess(AssignCategoryOutputData outputData) {
                System.out.println("Assigned recipes " + outputData.getAssignedRecipeIds()
                        + " to category " + outputData.getCategoryId());
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Assign failed: " + errorMessage);
            }
        };

        AssignCategoryInputBoundary assignInteractor =
                new AssignCategoryInteractor(categoryGateway, assignPresenter);

        assignInteractor.execute(
                new AssignCategoryInputData(userId, categoryId, Arrays.asList(101L, 103L)));

        // --- 3. Filter by category ---
        FilterByCategoryOutputBoundary filterPresenter = new FilterByCategoryOutputBoundary() {
            @Override
            public void presentSuccess(FilterByCategoryOutputData outputData) {
                System.out.println("Recipes in category " + categoryId + ":");
                for (SavedRecipe sr : outputData.getSavedRecipes()) {
                    System.out.println("  userId=" + sr.getUserId()
                            + ", recipeKey=" + sr.getRecipeKey());
                }
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Filter failed: " + errorMessage);
            }
        };

        FilterByCategoryInputBoundary filterInteractor =
                new FilterByCategoryInteractor(categoryGateway, savedGateway, filterPresenter);

        filterInteractor.execute(new FilterByCategoryInputData(userId, categoryId));
    }
}
