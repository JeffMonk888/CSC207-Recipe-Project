package interface_adapter.category;

import domain.entity.Category;
import domain.entity.SavedRecipe;
import usecase.category.assign_category.AssignCategoryOutputBoundary;
import usecase.category.assign_category.AssignCategoryOutputData;
import usecase.category.create_category.CreateCategoryOutputBoundary;
import usecase.category.create_category.CreateCategoryOutputData;
import usecase.category.delete_category.DeleteCategoryOutputBoundary;
import usecase.category.delete_category.DeleteCategoryOutputData;
import usecase.category.filter_by_category.FilterByCategoryOutputBoundary;
import usecase.category.filter_by_category.FilterByCategoryOutputData;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryOutputBoundary;
import usecase.category.remove_recipe.RemoveRecipeFromCategoryOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for UC10: Category management.
 *
 * This presenter is responsible for translating use case output data
 * into a CategoryState, and then notifying the view via the ViewModel.
 *
 * It implements all category-related OutputBoundary interfaces so that
 * a single presenter instance can be shared by all category use cases.
 */
public class CategoryPresenter implements
        CreateCategoryOutputBoundary,
        DeleteCategoryOutputBoundary,
        AssignCategoryOutputBoundary,
        FilterByCategoryOutputBoundary,
        RemoveRecipeFromCategoryOutputBoundary {

    private final CategoryViewModel viewModel;

    public CategoryPresenter(CategoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private CategoryState state() {
        return viewModel.getState();
    }

    // ===== Create Category =====

    @Override
    public void presentSuccess(CreateCategoryOutputData outputData) {
        CategoryState state = state();

        // Add the new category to the existing list.
        List<Category> categories = state.getCategories();
        categories.add(outputData.getCategory());
        state.setCategories(categories);

        state.setMessage("Created category: " + outputData.getCategory().getName());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Delete Category =====

    @Override
    public void presentSuccess(DeleteCategoryOutputData outputData) {
        CategoryState state = state();
        Long deletedId = outputData.getDeletedCategoryId();

        List<Category> remaining = new ArrayList<>();
        for (Category c : state.getCategories()) {
            if (c.getId() == null || !c.getId().equals(deletedId)) {
                remaining.add(c);
            }
        }
        state.setCategories(remaining);

        // Clear selection if we just deleted the selected category.
        if (deletedId != null && deletedId.equals(state.getSelectedCategoryId())) {
            state.setSelectedCategoryId(null);
        }

        state.setMessage("Deleted category with id: " + deletedId);
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Assign Recipes to Category =====

    @Override
    public void presentSuccess(AssignCategoryOutputData outputData) {
        CategoryState state = state();

        state.setSelectedCategoryId(outputData.getCategoryId());
        state.setAssignedRecipeIds(outputData.getAssignedRecipeIds());

        int count = outputData.getAssignedRecipeIds() == null
                ? 0
                : outputData.getAssignedRecipeIds().size();
        state.setMessage("Assigned " + count + " recipe(s) to category " +
                outputData.getCategoryId());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Filter by Category =====

    @Override
    public void presentSuccess(FilterByCategoryOutputData outputData) {
        CategoryState state = state();

        List<SavedRecipe> filtered = outputData.getSavedRecipes();
        state.setFilteredRecipes(filtered);

        // Typically this is a "neutral" operation, so we just clear errors.
        state.setMessage(null);
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Remove Recipe from Category =====

    @Override
    public void presentSuccess(RemoveRecipeFromCategoryOutputData outputData) {
        CategoryState state = state();

        List<SavedRecipe> filtered = state.getFilteredRecipes();
        List<SavedRecipe> remaining = new ArrayList<>();
        for (SavedRecipe sr : filtered) {
            if (!outputData.getRecipeId().equals(sr.getRecipeKey())) {
                remaining.add(sr);
            }
        }
        state.setFilteredRecipes(remaining);

        state.setMessage("Removed recipe " + outputData.getRecipeId() +
                " from category " + outputData.getCategoryId());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Common failure handler for all category use cases =====

    @Override
    public void presentFailure(String errorMessage) {
        CategoryState state = state();
        state.setErrorMessage(errorMessage);
        state.setMessage(null);

        viewModel.fireStateChanged();
    }
}
