package data.mapper;

import data.dto.RecipeInformationDto;
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

    public static Recipe toDomain(RecipeInformationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto is null");
        }

        // Build basic recipe (use your 10-arg constructor)
        Recipe recipe = new Recipe(
                dto.id,
                nullToEmpty(dto.title),
                "",
                dto.servings,
                dto.readyInMinutes,
                nullToEmpty(dto.sourceName),
                nullToEmpty(dto.sourceUrl),
                nullToEmpty(dto.image),
                "a" + dto.id,
                null

        );

        // Ingredients
        if (dto.ingredients != null) {
            for (RecipeInformationDto.ExtendedIngredient ingredientDTO : dto.ingredients) {
                String originalString = buildOriginalString(
                        ingredientDTO.amount,
                        ingredientDTO.unit,
                        ingredientDTO.name
                );

                recipe.addIngredient(new Ingredient(
                        null,
                        nullToEmpty(ingredientDTO.name),
                        ingredientDTO.amount,
                        nullToEmpty(ingredientDTO.unit),
                        originalString
                ));
            }

        }

        // Instruction steps
        if (dto.steps != null) {
            for (RecipeInformationDto.Step stepDto : dto.steps) {
                recipe.addInstructionStep(new InstructionStep(
                        null,
                        stepDto.number,
                        nullToEmpty(stepDto.step)
                ));
            }
        }

        // Nutrition
        final Double calories = dto.calories;

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
                null,
                calories,
                protein,
                fat,
                carbohydrates
        ));

        return recipe;
    }

    // helper functions below

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    private static String buildOriginalString(Double amount, String unit, String name) {
        final String amountPart = (amount == null) ? "" : stripTrailingZeros(amount);
        final String unitPart = (unit == null) ? "" : unit.trim();
        final String namePart = (name == null) ? "" : name.trim();

        StringBuilder sb = new StringBuilder();
        if (!amountPart.isEmpty()) {
            sb.append(amountPart);
        }

        if (!unitPart.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(unitPart);
        }
        if (!namePart.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
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
        final String result;
        if (d == null) {
            result = "";
        }
        else if (d == d.longValue()) {
            result = Long.toString(d.longValue());
        }
        else {
            result = Double.toString(d);
        }

        return result;
    }
}
