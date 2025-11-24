package usecase.add_ingredient;

public class AddIngredientOutputData {
    private final Long userId;
    private final String ingredient;

    public AddIngredientOutputData(Long userId, String ingredient) {
        this.userId = userId;
        this.ingredient = ingredient;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIngredient() {
        return ingredient;
    }
}
