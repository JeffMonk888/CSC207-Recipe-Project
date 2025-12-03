package interfaceadapter.fridge;

import java.util.ArrayList;
import java.util.List;

public class FridgeState {

    private String currentIngredient = "";
    private List<String> ingredients = new ArrayList<>();
    private String errorMessage;

    public String getCurrentIngredient() {
        return currentIngredient;
    }

    public void setCurrentIngredient(String currentIngredient) {
        this.currentIngredient = currentIngredient;
    }

    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = new ArrayList<>(ingredients);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
