package interface_adapter.category;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Presenter for category-related use cases.
 *
 * This presenter converts use case output data into the
 * {@link CategoryState} representation used by the GUI.
 */
public class CategoryPresenter implements
        CreateCategoryOutputBoundary,
        AssignCategoryOutputBoundary,
        FilterByCategoryOutputBoundary,
        RemoveRecipeFromCategoryOutputBoundary,
        DeleteCategoryOutputBoundary {

    private final CategoryViewModel viewModel;

    public CategoryPresenter(CategoryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // ===== Create Category =====

    @Override
    public void presentSuccess(CreateCategoryOutputData outputData) {
        CategoryState state = viewModel.getState();
        Category category = outputData.getCategory();

        List<String> categories = state.getCategories();
        String display = category.getId() + " - " + category.getName();
        categories.add(display);
        state.setCategories(categories);

        state.setInfoMessage("Created category: " + category.getName());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Assign Category =====

    @Override
    public void presentSuccess(AssignCategoryOutputData outputData) {
        CategoryState state = viewModel.getState();
        state.setSelectedCategoryId(outputData.getCategoryId());
        state.setInfoMessage("Assigned recipes "
                + outputData.getAssignedRecipeIds()
                + " to category " + outputData.getCategoryId());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Filter By Category =====

    @Override
    public void presentSuccess(FilterByCategoryOutputData outputData) {
        CategoryState state = viewModel.getState();

        List<String> recipeTexts = new ArrayList<>();
        for (SavedRecipe sr : outputData.getSavedRecipes()) {
            String text = sr.getRecipeKey();
            if (sr.isFavourite()) {
                text = text + " \u2605"; // add a star symbol
            }
            recipeTexts.add(text);
        }

        state.setFilteredRecipes(recipeTexts);
        state.setFiltered(true);
        state.setInfoMessage(null);
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Remove Recipe From Category =====

    @Override
    public void presentSuccess(RemoveRecipeFromCategoryOutputData outputData) {
        CategoryState state = viewModel.getState();
        state.setSelectedCategoryId(outputData.getCategoryId());
        state.setInfoMessage("Removed recipe "
                + outputData.getRecipeId()
                + " from category " + outputData.getCategoryId());
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Delete Category =====

    @Override
    public void presentSuccess(DeleteCategoryOutputData outputData) {
        CategoryState state = viewModel.getState();
        Long deletedId = outputData.getDeletedCategoryId();

        List<String> remaining = new ArrayList<>();
        for (String cat : state.getCategories()) {
            if (!cat.startsWith(deletedId + " -")) {
                remaining.add(cat);
            }
        }
        state.setCategories(remaining);

        state.setInfoMessage("Deleted category " + deletedId);
        state.setErrorMessage(null);

        viewModel.fireStateChanged();
    }

    // ===== Generic Failure =====

    /**
     * Handle failures for all category-related use cases.
     *
     * @param errorMessage a human-readable error message
     */
    @Override
    public void presentFailure(String errorMessage) {
        CategoryState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setInfoMessage(null);

        viewModel.fireStateChanged();
    }
}
