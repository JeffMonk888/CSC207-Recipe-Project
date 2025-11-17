package data.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeInformationDTO {
    public long id;
    public String title;
    public String image;
    public Integer servings;
    public Integer readyInMinutes;
    public String sourceName;
    public String sourceUrl;

    // Ingradients
    public List<ExtendedIngredient> ingredients = new ArrayList<>();

    // Instructions
    public List<Step> steps = new ArrayList<>();

    // Nutrition
    public Double calories;
    public Double proteinAmount;
    public String proteinUnit;
    public Double fatAmount;
    public String fatUnit;
    public Double carbsAmount;
    public String carbsUnit;

    public static class ExtendedIngredient {
        public String name;
        public Double amount;
        public String unit;
        public String original;
    }

    public static class Step {
        public int number;
        public String step;
    }


}
