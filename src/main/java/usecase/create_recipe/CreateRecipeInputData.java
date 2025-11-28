package usecase.create_recipe;

public class CreateRecipeInputData {
    private final Long userId;
    private final String title;
    private final String ingredients;
    private final String instructions;

    public CreateRecipeInputData(Long userId, String title, String ingredients, String instructions) {
        this.userId = userId;
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
}
