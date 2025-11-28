package interface_adapter.create_recipe;

public class CreateRecipeState {
    private String title = "";
    private String ingredients = "";
    private String instructions = "";
    private String message = null;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
