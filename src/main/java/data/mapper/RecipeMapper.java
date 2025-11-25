package data.mapper;

import data.dto.RecipeInformationDTO;
import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import domain.entity.Recipe;

/**
 * Maps Spoonacular RecipeInformationDTO -> domain.entity.Recipe
 * Aligns with the 10-arg Recipe(...) constructor in your domain.
 * - image omitted
 * - description not provided by API (empty string)
 * - prepTimeInMinutes <- readyInMinutes
 * - apiId = String.valueOf(dto.id)
 * - NutritionInfo uses Double calories + String macros ("20g" style)
 */
public final class RecipeMapper {


    public static Recipe toDomain(RecipeInformationDTO dto) {
        if (dto == null) throw new IllegalArgumentException("dto is null");

        // Build basic recipe (use your 10-arg constructor)
        Recipe recipe = new Recipe(
                dto.id,                              // id (Spoonacular id)
                nullToEmpty(dto.title),                       // title
                "",                                   // description (API rarely provides; keep empty)
                dto.servings,                        // servings
                dto.readyInMinutes,                  // prepTimeInMinutes <- readyInMinutes
                nullToEmpty(dto.sourceName),                  // sourceName
                nullToEmpty(dto.sourceUrl),                   // sourceUrl
                nullToEmpty(dto.image),                       // image (omitted for now)
                "a" + dto.id,              // apiId (string form of API id)
                null                                 // nutritionInfo set below

        );

        // Ingredients
        if (dto.ingredients != null) {
            for (RecipeInformationDTO.ExtendedIngredient ingredientDTO : dto.ingredients) {
                String originalString = buildOriginalString(
                        ingredientDTO.amount,
                        ingredientDTO.unit,
                        ingredientDTO.name
                );

                recipe.addIngredient(new Ingredient(
                        null,                        // id (don't need it for now)
                        nullToEmpty(ingredientDTO.name),
                        ingredientDTO.amount,           // may be null
                        nullToEmpty(ingredientDTO.unit),
                        originalString
                ));
            }

        }

        // Instruction steps
        if (dto.steps != null) {
            for (RecipeInformationDTO.Step stepDTO : dto.steps) {
                recipe.addInstructionStep(new InstructionStep(
                        null,                         // id (null for now)
                        stepDTO.number,                   // stepNumber
                        nullToEmpty(stepDTO.step)         // description
                ));
            }
        }

        // Nutrition
        Double calories = dto.calories;

        String protein = dto.proteinAmount == null
                ? null
                : formatAmount(dto.proteinAmount, dto.proteinUnit);

        String fat = dto.fatAmount == null
                ? null
                : formatAmount(dto.fatAmount, dto.fatUnit);

        String carbohydrates = dto.carbsAmount == null
                ? null
                : formatAmount(dto.carbsAmount, dto.carbsUnit);

        recipe.setNutritionInfo(new NutritionInfo(
                null,            // id (null for now)
                calories,        // Double kcal
                protein,         // e.g. "20g"
                fat,             // e.g. "15g"
                carbohydrates    // e.g. "30g"
        ));

        return recipe;
    }

    // helper functions below

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    private static String buildOriginalString(Double amount, String unit, String name) {
        String amountPart = (amount == null) ? "" : stripTrailingZeros(amount);
        String unitPart = (unit == null) ? "" : unit.trim();
        String namePart = (name == null) ? "" : name.trim();

        StringBuilder sb = new StringBuilder();
        if (!amountPart.isEmpty()) sb.append(amountPart);
        if (!unitPart.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(unitPart);
        }
        if (!namePart.isEmpty()) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(namePart);
        }
        return sb.toString();
    }

    private static String formatAmount(Double amount, String unit) {
        if (amount == null && (unit == null || unit.isBlank())) return null;
        if (amount == null) return unit.trim();
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
