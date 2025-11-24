package usecase.retrieve_saved;

import domain.entity.Recipe; // <-- 更改
import java.util.List; // <-- 更改

public class RetrieveSavedOutputData {
    private final List<Recipe> savedRecipes;

    public RetrieveSavedOutputData(List<Recipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public List<Recipe> getSavedRecipes() {
        return savedRecipes;
    }
}
