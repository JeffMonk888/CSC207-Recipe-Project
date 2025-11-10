package domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    // Attributes
    private Long id;
    private String title;
    private String description;
    private Integer servings;
    private Integer prepTimeInMinutes; //
    private String sourceName; //
    private String sourceUrl; // URL
    private String image; // URL to the image
    private String apiId; // external API recipe id (nullable if local）

    private List<Ingredient> ingredients;

    private List<InstructionStep> instructionSteps;

    private NutritionInfo nutritionInfo;

    public Recipe(Long id, String title, String description, Integer servings,
                  Integer prepTimeInMinutes, String sourceName, String sourceUrl,
                  String image, String apiId, NutritionInfo nutritionInfo) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.servings = servings;
        this.prepTimeInMinutes = prepTimeInMinutes;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.image = image;
        this.apiId = apiId;

        this.nutritionInfo = nutritionInfo;
        this.ingredients = new ArrayList<>();
        this.instructionSteps = new ArrayList<>();
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getServings() { return servings; }
    public Integer getPrepTimeInMinutes() { return prepTimeInMinutes; }
    public String getSourceName() { return sourceName; }
    public String getSourceUrl() { return sourceUrl; }
    public String getImage() { return image; }
    public String getApiId() { return apiId; }
    public NutritionInfo getNutritionInfo() { return nutritionInfo; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<InstructionStep> getInstructionSteps() { return instructionSteps; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setServings(Integer servings) { this.servings = servings; }
    public void setPrepTimeInMinutes(Integer prepTimeInMinutes) { this.prepTimeInMinutes = prepTimeInMinutes; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public void setImage(String image) { this.image = image; }
    public void setApiId(String apiId) { this.apiId = apiId; }
    public void setNutritionInfo(NutritionInfo nutritionInfo) { this.nutritionInfo = nutritionInfo; }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    public void addInstructionStep(InstructionStep step) {
        this.instructionSteps.add(step);
    }

    public void removeInstructionStep(InstructionStep step) {
        this.instructionSteps.remove(step);
    }

    @Override
    public String toString() {
        return "domain.entity.Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
