package data.mapper;

import data.dto.RecipeInformationDTO;
import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import domain.entity.Recipe;

/**
 * Maps Spoonacular RecipeInformationDTO -> domain.entity.Recipe
 * Aligns with the 10-arg Recipe(...) constructor in your domain.
 * - image omitted (null)
 * - description not provided by API (empty string)
 * - prepTimeInMinutes <- readyInMinutes
 * - apiId = String.valueOf(dto.id)
 * - NutritionInfo uses Double calories + String macros ("20g" style)
 */
public final class RecipeMapper {

    private RecipeMapper() {}

    public static Recipe toDomain(RecipeInformationDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto is null");

        // Build basic recipe (use your 10-arg constructor)
        Recipe recipe = new Recipe(
                dto.id,                              // id (Spoonacular id)
                nn(dto.title),                       // title
                "",                                   // description (API rarely provides; keep empty)
                dto.servings,                        // servings
                dto.readyInMinutes,                  // prepTimeInMinutes <- readyInMinutes
                nn(dto.sourceName),                  // sourceName
                nn(dto.sourceUrl),                   // sourceUrl
                null,                                 // image (omitted for now)
                String.valueOf(dto.id),              // apiId (string form of API id)
                null                                  // nutritionInfo set below
        );

        // Ingredients
        if (dto.extendedIngredients != null) {
            for (var ei : dto.extendedIngredients) {
                recipe.addIngredient(new Ingredient(
                        null,                         // id (null for now)
                        nn(ei.name),
                        ei.amount,                    // may be null -> allowed by your domain
                        nn(ei.unit),
                        nn(ei.original)
                ));
            }
        }

        // Instruction steps (DTO already flattened to steps list)
        if (dto.steps != null) {
            for (var s : dto.steps) {
                recipe.addInstructionStep(new InstructionStep(
                        null,                         // id (null for now)
                        s.number,                     // stepNumber
                        nn(s.step)                    // description
                ));
            }
        }

        // Nutrition: pick common nutrients; format macros as "amount + unit"
        Double calories = null;
        String protein = null, fat = null, carbohydrates = null;

        if (dto.nutrients != null) {
            for (var n : dto.nutrients) {
                String key = nn(n.name).toLowerCase();
                if ("calories".equals(key)) {
                    calories = n.amount; // kcal (Double)
                } else if ("protein".equals(key)) {
                    protein = formatAmount(n.amount, n.unit); // "20g"
                } else if ("fat".equals(key)) {
                    fat = formatAmount(n.amount, n.unit);
                } else if ("carbohydrates".equals(key)) {
                    carbohydrates = formatAmount(n.amount, n.unit);
                }
            }
        }

        recipe.setNutritionInfo(new NutritionInfo(
                null,            // id (null for now)
                calories,        // Double
                protein,         // "20g"
                fat,             // "15g"
                carbohydrates    // "30g"
        ));

        return recipe;
    }

    // --- helpers ---
    private static String nn(String s) {
        return (s == null) ? "" : s;
    }

    private static String formatAmount(Double amount, String unit) {
        if (amount == null && (unit == null || unit.isBlank())) return null;
        if (amount == null) return unit == null ? null : unit.trim();
        String u = (unit == null) ? "" : unit.trim();
        // compact formatting: "20 g" -> "20g" if unit is a simple token
        if (!u.isEmpty() && !u.startsWith(" ") && !Character.isWhitespace(u.charAt(0))) {
            return stripTrailingZeros(amount) + u;
        }
        return stripTrailingZeros(amount) + (u.isEmpty() ? "" : (" " + u));
    }

    private static String stripTrailingZeros(Double d) {
        if (d == null) return "";
        if (d == d.longValue()) return Long.toString(d.longValue());
        return Double.toString(d);
    }
}
