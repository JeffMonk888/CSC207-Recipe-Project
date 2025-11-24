package usecase.add_ingredient;

public class AddIngredientInputData {
    private final Long userId;
    private final String ingredient;

    public AddIngredientInputData(Long userId, String ingredient) {
        this.userId = userId;
        this.ingredient = ingredient;
    }

    Long getUserId() {
        return userId;
    }

    String getIngredient() {
        return ingredient;
    }
}
