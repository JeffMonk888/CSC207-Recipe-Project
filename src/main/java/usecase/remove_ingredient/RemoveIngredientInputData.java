package usecase.remove_ingredient;

public class RemoveIngredientInputData {

    private final Long userId;
    private final String ingredient;

    public RemoveIngredientInputData(Long userId, String ingredient) {
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
