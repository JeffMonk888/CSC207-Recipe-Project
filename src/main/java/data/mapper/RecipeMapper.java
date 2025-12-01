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

    public static final String SPACE = " ";

    /**
     * Convert RecipeInformationDto into a fully constructed Recipe domain entity.
     *
     * @param dto the DTO representing Spoonacular recipe information; must not be {@code null}
     * @return a new Recipe domain object populated with all data extracted from the DTO
     * @throws IllegalArgumentException if {@code dto} is {@code null}
     */

    public static Recipe toDomain(RecipeInformationDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("dto is null");
        }

        // Build basic recipe (use your 10-arg constructor)
        final Recipe recipe = new Recipe(
                dto.getId(),
                nullToEmpty(dto.getTitle()),
                "",
                dto.getServings(),
                dto.getReadyInMinutes(),
                nullToEmpty(dto.getSourceName()),
                nullToEmpty(dto.getSourceUrl()),
                nullToEmpty(dto.getImage()),
                "a" + dto.getId(),
                null

        );

        // Ingredients
        if (dto.getIngredients() != null) {
            for (RecipeInformationDto.ExtendedIngredient ingredientDto : dto.getIngredients()) {
                final String originalString = buildOriginalString(
                        ingredientDto.getAmount(),
                        ingredientDto.getUnit(),
                        ingredientDto.getName()
                );

                recipe.addIngredient(new Ingredient(
                        null,
                        nullToEmpty(ingredientDto.getName()),
                        ingredientDto.getAmount(),
                        nullToEmpty(ingredientDto.getUnit()),
                        originalString
                ));
            }

        }

        // Instruction steps
        if (dto.getSteps() != null) {
            for (RecipeInformationDto.Step stepDto : dto.getSteps()) {
                recipe.addInstructionStep(new InstructionStep(
                        null,
                        stepDto.getNumber(),
                        nullToEmpty(stepDto.getStep())
                ));
            }
        }

        // Nutrition
        final Double calories = dto.getCalories();

        String protein = null;
        if (dto.getProteinAmount() != null) {
            protein = formatAmount(dto.getProteinAmount(), dto.getProteinUnit());
        }

        String fat = null;
        if (dto.getFatAmount() != null) {
            fat = formatAmount(dto.getFatAmount(), dto.getFatUnit());
        }

        String carbohydrates = null;
        if (dto.getCarbsAmount() != null) {
            carbohydrates = formatAmount(dto.getCarbsAmount(), dto.getCarbsUnit());
        }

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

    private static String nullToEmpty(String string) {
        String result = "";
        if (string != null) {
            result = string;
        }
        return result;
    }

    private static String buildOriginalString(Double amount, String unit, String name) {

        String amountPart = "";
        if (amount != null) {
            amountPart = stripTrailingZeros(amount);
        }

        String unitPart = "";
        if (name != null) {
            unitPart = unit.trim();
        }

        String namePart = "";
        if (name != null) {
            namePart = name.trim();
        }

        final StringBuilder sb = new StringBuilder();
        if (!amountPart.isEmpty()) {
            sb.append(amountPart);
        }

        if (!unitPart.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(SPACE);
            }
            sb.append(unitPart);
        }
        if (!namePart.isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(SPACE);
            }
            sb.append(namePart);
        }
        return sb.toString();
    }

    private static String formatAmount(Double amount, String unit) {
        final String result;

        if (isAmountAndUnitEmpty(amount, unit)) {
            result = null;
        }
        else if (amount == null) {
            result = formatUnitOnly(unit);
        }
        else {
            result = formatAmountAndUnit(amount, unit);
        }

        return result;
    }

    private static boolean isAmountAndUnitEmpty(Double amount, String unit) {
        final boolean amountIsNull = amount == null;
        final boolean unitIsNull = unit == null;
        final boolean unitIsBlank = !unitIsNull && unit.isBlank();

        return amountIsNull && (unitIsNull || unitIsBlank);
    }

    private static String formatUnitOnly(String unit) {
        String result = null;
        if (unit != null) {
            result = unit.trim();
        }
        return result;
    }

    private static String formatAmountAndUnit(Double amount, String unit) {
        String trimmedUnit = "";
        if (unit != null) {
            trimmedUnit = unit.trim();
        }

        final String result;
        if (!trimmedUnit.isEmpty()
                && !trimmedUnit.startsWith(SPACE)
                && !Character.isWhitespace(trimmedUnit.charAt(0))) {
            // compact: "20g"
            result = stripTrailingZeros(amount) + trimmedUnit;
        }
        else if (trimmedUnit.isEmpty()) {
            // just the amount: "20"
            result = stripTrailingZeros(amount);
        }
        else {
            // standard: "20 g"
            result = stripTrailingZeros(amount) + SPACE + trimmedUnit;
        }

        return result;
    }

    private static String stripTrailingZeros(Double aDouble) {
        final String result;
        if (aDouble == null) {
            result = "";
        }
        else if (aDouble == aDouble.longValue()) {
            result = Long.toString(aDouble.longValue());
        }
        else {
            result = Double.toString(aDouble);
        }

        return result;
    }
}
