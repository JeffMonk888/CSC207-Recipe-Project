package interfaceadapter.saved_recipe;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipeState {

    private List<String> savedRecipes = new ArrayList<>();
    private String errorMessage;

    public SavedRecipeState() {
    }

    public SavedRecipeState(SavedRecipeState copy) {
        this.savedRecipes = new ArrayList<>(copy.savedRecipes);
        this.errorMessage = copy.errorMessage;
    }

    public List<String> getSavedRecipes() {
        return savedRecipes;
    }

    public void setSavedRecipes(List<String> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
