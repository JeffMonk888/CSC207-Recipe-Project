package usecase.retrieve_saved;

import domain.entity.Recipe;
import java.util.List;

public class RetrieveSavedOutputData {
    private final List<Recipe> savedRecipes;

    public RetrieveSavedOutputData(List<Recipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public List<Recipe> getSavedRecipes() {

        return savedRecipes;
    }
}
