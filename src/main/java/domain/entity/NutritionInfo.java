package domain.entity;
import org.json.JSONObject;

public class NutritionInfo {
    private Long id;
    private Double calories; // per serving 
    private String protein; // e.g., "20g" 
    private String fat; // e.g., "15g" 
    private String carbohydrates; // e.g., "30g" 

    public NutritionInfo(Long id, Double calories, String protein, String fat, String carbohydrates) {
        this.id = id;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    // Situation for getting recipe from JSON
    public NutritionInfo(JSONObject jsonObject) {
        this.id = jsonObject.getLong("id");
        this.calories = jsonObject.getDouble("calories");
        this.protein = jsonObject.getString("protein");
        this.fat = jsonObject.getString("fat");
        this.carbohydrates = jsonObject.getString("carbohydrates");
    }

    // create a JSON for recipe
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("calories", calories);
        jsonObject.put("protein", protein);
        jsonObject.put("fat", fat);
        jsonObject.put("carbohydrates", carbohydrates);
        return jsonObject;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }
    public String getProtein() { return protein; }
    public void setProtein(String protein) { this.protein = protein; }
    public String getFat() { return fat; }
    public void setFat(String fat) { this.fat = fat; }
    public String getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(String carbohydrates) { this.carbohydrates = carbohydrates; }

    @Override
    public String toString() {
        return "Nutrition: " +
                "Calories=" + calories +
                ", Protein='" + protein + '\'' +
                ", Fat='" + fat + '\'' +
                ", Carbs='" + carbohydrates + '\'';
    }

}
