package data.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeInformationDTO {
    public long id;
    public String title;
//    public String image;
    public Integer servings;
    public Integer readyInMinutes;
    public String sourceName;
    public String sourceUrl;

    public List<ExtendedIngredient> extendedIngredients = new ArrayList<>();
    public List<Step> steps = new ArrayList<>();
    public List<Nutrient> nutrients = new ArrayList<>();

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

    public static class Nutrient {
        public String name;
        public Double amount;
        public String unit;
    }
}
