package usecase.retrieve_saved;

import domain.entity.SavedRecipe;

import java.util.ArrayList;

// --- Output Data ---
public class RetrieveSavedOutputData {
    private final ArrayList<SavedRecipe> savedRecipes;

    public RetrieveSavedOutputData(ArrayList<SavedRecipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }
    public ArrayList<SavedRecipe> getSavedRecipes() { return savedRecipes; }
}
