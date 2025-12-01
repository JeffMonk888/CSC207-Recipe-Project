package data.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing detailed recipe information
 * retrieved from the Spoonacular API.
 *
 * <p>This object mirrors the structure of the API response but contains no
 * business logic. It is used exclusively for transferring data from the
 * data layer to the application layer.</p>
 */
public class RecipeInformationDto {

    private long id;
    private String title;
    private String image;
    private Integer servings;
    private Integer readyInMinutes;
    private String sourceName;
    private String sourceUrl;

    // Ingredients
    private final List<ExtendedIngredient> ingredients = new ArrayList<>();

    // Instructions
    private final List<Step> steps = new ArrayList<>();

    // Nutrition
    private Double calories;
    private Double proteinAmount;
    private String proteinUnit;
    private Double fatAmount;
    private String fatUnit;
    private Double carbsAmount;
    private String carbsUnit;

    // ===== Getters and Setters for main DTO fields =====

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public List<ExtendedIngredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getProteinAmount() {
        return proteinAmount;
    }

    public void setProteinAmount(Double proteinAmount) {
        this.proteinAmount = proteinAmount;
    }

    public String getProteinUnit() {
        return proteinUnit;
    }

    public void setProteinUnit(String proteinUnit) {
        this.proteinUnit = proteinUnit;
    }

    public Double getFatAmount() {
        return fatAmount;
    }

    public void setFatAmount(Double fatAmount) {
        this.fatAmount = fatAmount;
    }

    public String getFatUnit() {
        return fatUnit;
    }

    public void setFatUnit(String fatUnit) {
        this.fatUnit = fatUnit;
    }

    public Double getCarbsAmount() {
        return carbsAmount;
    }

    public void setCarbsAmount(Double carbsAmount) {
        this.carbsAmount = carbsAmount;
    }

    public String getCarbsUnit() {
        return carbsUnit;
    }

    public void setCarbsUnit(String carbsUnit) {
        this.carbsUnit = carbsUnit;
    }

    // ===== Helper methods for SpoonacularClient =====

    /**
     * Adds a new ingredient to this recipe.
     *
     * <p>A new {@link ExtendedIngredient} instance is created from the
     * supplied values and added to the internal ingredient list.</p>
     *
     * @param name     the ingredient name (not {@code null})
     * @param amount   the numeric quantity of the ingredient (may be {@code null})
     * @param unit     the measurement unit (may be {@code null})
     * @param original the raw unformatted representation from the API (may be {@code null})
     */

    public void addIngredient(String name, Double amount, String unit, String original) {
        final ExtendedIngredient ing = new ExtendedIngredient();
        ing.setName(name);
        ing.setAmount(amount);
        ing.setUnit(unit);
        ing.setOriginal(original);
        ingredients.add(ing);
    }

    /**
     * Adds a new instruction step to this recipe.
     *
     * <p>A new {@link Step} instance is constructed using the provided
     * step number and instruction text and appended to the step list.</p>
     *
     * @param number   the step number in sequence
     * @param stepText the description of the action for this step
     */
    public void addStep(int number, String stepText) {
        final Step step = new Step();
        step.setNumber(number);
        step.setStep(stepText);
        steps.add(step);
    }

    public static class Step {

        private int number;
        private String step;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }
    }

    public static class ExtendedIngredient {

        private String name;
        private Double amount;
        private String unit;
        private String original;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

}
