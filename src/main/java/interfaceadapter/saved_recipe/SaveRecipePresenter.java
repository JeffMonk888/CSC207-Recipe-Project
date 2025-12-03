package interfaceadapter.saved_recipe;

import usecase.save_recipe.SaveRecipeOutputBoundary;
import usecase.save_recipe.SaveRecipeOutputData;


public class SaveRecipePresenter implements SaveRecipeOutputBoundary {

    @Override
    public void presentSuccess(SaveRecipeOutputData outputData) {
        System.out.println("Recipe saved successfully: " + outputData.getSavedRecipe().getRecipeKey());
    }

    @Override
    public void presentFailure(String errorMessage) {
        System.err.println("Failed to save: " + errorMessage);
    }
}