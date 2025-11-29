package usecase.category.filter_by_category;

import domain.entity.SavedRecipe;
import usecase.category.CategoryDataAccessInterface;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterByCategoryInteractor implements FilterByCategoryInputBoundary {

    private final CategoryDataAccessInterface categoryGateway;
    private final MotionForRecipe savedRecipeGateway;
    private final FilterByCategoryOutputBoundary presenter;

    public FilterByCategoryInteractor(CategoryDataAccessInterface categoryGateway,
                                      MotionForRecipe savedRecipeGateway,
                                      FilterByCategoryOutputBoundary presenter) {
        this.categoryGateway = categoryGateway;
        this.savedRecipeGateway = savedRecipeGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(FilterByCategoryInputData inputData) {
        Long userId = inputData.getUserId();
        Long categoryId = inputData.getCategoryId();

        if (!categoryGateway.categoryExistsForUser(userId, categoryId)) {
            presenter.presentFailure("Category not found for this user.");
            return;
        }

        List<Long> recipeIds = categoryGateway.getRecipeIdsForCategory(userId, categoryId);
        if (recipeIds == null || recipeIds.isEmpty()) {
            presenter.presentSuccess(new FilterByCategoryOutputData(new ArrayList<>()));
            return;
        }

        Set<String> recipeKeySet = new HashSet<>();
        for (Long rid : recipeIds) {
            recipeKeySet.add(String.valueOf(rid));
        }

        ArrayList<SavedRecipe> allSaved = savedRecipeGateway.findByUserId(userId);
        ArrayList<SavedRecipe> filtered = new ArrayList<>();

        for (SavedRecipe sr : allSaved) {
            if (recipeKeySet.contains(sr.getRecipeKey())) {
                filtered.add(sr);
            }
        }

        presenter.presentSuccess(new FilterByCategoryOutputData(filtered));
    }
}
