package domain.entity;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

public class Recipe {
    // Attributes
    private Long id;
    private String title;
    private String description;
    private Integer servings;
    private Integer prepTimeInMinutes;
    private String sourceName;
    private String sourceUrl;
    private String image;
    private String recipeKey;

    private ArrayList<Ingredient> ingredients;

    private ArrayList<InstructionStep> instructionSteps;

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
        this.recipeKey = apiId;
        this.nutritionInfo = nutritionInfo;
        this.ingredients = new ArrayList<>();
        this.instructionSteps = new ArrayList<>();
    }

    public Recipe(JSONObject obj) {
        this.id = obj.has("id") ? obj.getLong("id") : null; // ensure it will not cause some error when read
        this.title = obj.getString("title");
        this.description = obj.getString("description");
        this.servings = obj.has("servings") ? obj.getInt("servings") : null;
        this.prepTimeInMinutes = obj.has("prepTimeInMinutes") ? obj.getInt("prepTimeInMinutes") : null;
        this.sourceName = obj.getString("sourceName");
        this.sourceUrl = obj.getString("sourceUrl");
        this.image = obj.getString("image");
        this.recipeKey = obj.has("recipeid") ? obj.optString("recipeid", null) : null;
        this.recipeKey = obj.optString("recipeid", null);

        if (obj.has("nutritionInfo")) {
            this.nutritionInfo = new NutritionInfo(obj.getJSONObject("nutritionInfo"));
        }

        this.ingredients = new ArrayList<>();
        if (obj.has("ingredients")) {
            JSONArray ingArray = obj.getJSONArray("ingredients");
            for (int i = 0; i < ingArray.length(); i++) {
                this.ingredients.add(new Ingredient(ingArray.getJSONObject(i)));
            }
        }

        this.instructionSteps = new ArrayList<>();
        if (obj.has("instructionSteps")) {
            JSONArray stepArray = obj.getJSONArray("instructionSteps");
            for (int i = 0; i < stepArray.length(); i++) {
                this.instructionSteps.add(new InstructionStep(stepArray.getJSONObject(i)));
            }
        }
    }

    public JSONObject toJson() {
        final JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("description", description);
        obj.put("servings", servings);
        obj.put("prepTimeInMinutes", prepTimeInMinutes);
        obj.put("sourceName", sourceName);
        obj.put("sourceUrl", sourceUrl);
        obj.put("image", image);
        obj.put("recipeid", recipeKey);

        if (nutritionInfo != null) {
            obj.put("nutritionInfo", nutritionInfo.toJson());
        }

        JSONArray iArray = new JSONArray();
        for (Ingredient i : ingredients) {
            iArray.put(i.toJson());
        }
        obj.put("ingredients", iArray);

        final JSONArray stepArray = new JSONArray();
        for (InstructionStep step : instructionSteps) {
            stepArray.put(step.toJson());
        }
        obj.put("instructionSteps", stepArray);

        return obj;
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
    public NutritionInfo getNutritionInfo() { return nutritionInfo; }
    public ArrayList<Ingredient> getIngredients() { return ingredients; }
    public ArrayList<InstructionStep> getInstructionSteps() { return instructionSteps; }
    public String getRecipeKey() { return recipeKey; }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public void setPrepTimeInMinutes(Integer prepTimeInMinutes) {
        this.prepTimeInMinutes = prepTimeInMinutes;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setNutritionInfo(NutritionInfo nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
    }

    /**
     * Add ingredient of a certain recipe into the ingredient list of all the ingredients.
     *
     * @param ingredient a valid filled Ingredient class
     */
    public void addIngredient(Ingredient ingredient) {

        this.ingredients.add(ingredient);
    }

    /**
     * Remove ingredient from the ingredient list.
     *
     * @param ingredient a valid filled Ingredient class
     */
    public void removeIngredient(Ingredient ingredient) {

        this.ingredients.remove(ingredient);
    }

    /**
     *  Add a specific step into the instruction list that is required to make the recipe given.
     *
     * @param step a valid InstructionStep class
     */
    public void addInstructionStep(InstructionStep step) {

        this.instructionSteps.add(step);
    }

    /**
     *  Remove a specific step that is in the Instruction list that is no longer required.
     *
     * @param step a valid InstructionStep class
     */
    public void removeInstructionStep(InstructionStep step) {
        this.instructionSteps.remove(step);
    }

    @Override
    public String toString() {
        return "domain.entity.Recipe{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", sourceName='" + sourceName + '\''
                + '}';
    }
}
