package usecase.category;

import domain.entity.SavedRecipe;

import java.util.List;

public class FilterByCategoryOutputData {

    private final List<SavedRecipe> savedRecipes;

    public FilterByCategoryOutputData(List<SavedRecipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public List<SavedRecipe> getSavedRecipes() {
        return savedRecipes;
    }
}
