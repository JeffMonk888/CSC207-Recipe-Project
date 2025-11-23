package interface_adapter.view_recipe;
import java.util.ArrayList;
import java.util.List;

public class ViewRecipeState {

    private Long recipeId;
    private String title;
    private String sourceName;
    private String sourceUrl;
    private Integer servings;
    private Integer readyInMinutes;
    private String imageUrl;
    private String calories;
    private String protein;
    private String fat;
    private String carbohydrates;

    private List<String> ingredients = new ArrayList<>();
    private List<String> steps = new ArrayList<>();

    private String errorMessage;

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }

    public Integer getReadyInMinutes() { return readyInMinutes; }
    public void setReadyInMinutes(Integer readyInMinutes) { this.readyInMinutes = readyInMinutes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCalories() { return calories; }
    public void setCalories(String calories) { this.calories = calories; }

    public String getProtein() { return protein; }
    public void setProtein(String protein) { this.protein = protein; }

    public String getFat() { return fat; }
    public void setFat(String fat) { this.fat = fat; }

    public String getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(String carbohydrates) { this.carbohydrates = carbohydrates; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
