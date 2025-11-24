import data.category.InMemoryCategoryGateway;
import data.saved_recipe.InMemorySavedRecipeGateway;
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
        MotionForRecipe savedGateway = new InMemorySavedRecipeGateway();

        // 先假装用户已经保存了三道菜
        savedGateway.save(new SavedRecipe(userId, 101L));
        savedGateway.save(new SavedRecipe(userId, 102L));
        savedGateway.save(new SavedRecipe(userId, 103L));

        // --- 1. 创建分类 ---
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

        // 拿到刚刚创建的分类 id
        Long categoryId = categoryGateway.findCategoriesForUser(userId).get(0).getId();

        // --- 2. 给分类分配菜谱 ---
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

        assignInteractor.execute(new AssignCategoryInputData(
                userId,
                categoryId,
                Arrays.asList(101L, 103L)
        ));

        // --- 3. 按分类过滤我的菜谱 ---
        FilterByCategoryOutputBoundary filterPresenter = new FilterByCategoryOutputBoundary() {
            @Override
            public void presentSuccess(FilterByCategoryOutputData outputData) {
                System.out.println("Recipes in category 'Quick Meals':");
                for (SavedRecipe sr : outputData.getSavedRecipes()) {
                    System.out.println("  recipeId = " + sr.getRecipeId());
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
