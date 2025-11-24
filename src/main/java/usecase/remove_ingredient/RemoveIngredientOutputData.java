package usecase.remove_ingredient;

public class RemoveIngredientOutputData {

    private final String removedIngredient;

    public RemoveIngredientOutputData(String removedIngredient) {
        this.removedIngredient = removedIngredient;
    }

    public String getRemovedIngredient() {
        return removedIngredient;
    }
}
