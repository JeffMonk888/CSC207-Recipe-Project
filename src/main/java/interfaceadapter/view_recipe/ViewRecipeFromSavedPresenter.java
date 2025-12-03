package interfaceadapter.view_recipe;

import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import domain.entity.Recipe;
import interfaceadapter.ViewManagerModel;
import usecase.view_recipe.ViewRecipeOutputBoundary;
import usecase.view_recipe.ViewRecipeOutputData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Presenter for UC5 when opened from SavedRecipesView.
 *
 * Same as ViewRecipePresenter, but switches to the "no-save" view
 * used by ViewRecipeNoSave.
 */
public class ViewRecipeFromSavedPresenter implements ViewRecipeOutputBoundary {

    // Must match ViewRecipeNoSave.VIEW_NAME
    private static final String SAVED_VIEW_NAME = "view_recipe_from_saved";

    private final ViewRecipeAbstractViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewRecipeFromSavedPresenter(ViewRecipeAbstractViewModel viewModel,
                                        ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void presentSuccess(ViewRecipeOutputData outputData) {
        Recipe recipe = outputData.getRecipe();

        ViewRecipeState state = new ViewRecipeState();

        if (recipe != null) {
            // Basic metadata
            try {
                state.setRecipeId(recipe.getId());
            } catch (NoSuchMethodError e) {
                // If Recipe has no getId(), ignore.
            }

            state.setTitle(recipe.getTitle());
            state.setSourceName(recipe.getSourceName());
            state.setSourceUrl(recipe.getSourceUrl());
            state.setServings(recipe.getServings());
            state.setReadyInMinutes(recipe.getPrepTimeInMinutes());
            state.setImageUrl(recipe.getImage());

            // Nutrition
            NutritionInfo n = recipe.getNutritionInfo();
            if (n != null) {
                if (n.getCalories() != null) {
                    state.setCalories(n.getCalories().toString());
                }
                state.setProtein(n.getProtein());
                state.setFat(n.getFat());
                state.setCarbohydrates(n.getCarbohydrates());
            }

            // Ingredients
            List<String> ingredientLines = new ArrayList<>();
            if (recipe.getIngredients() != null) {
                for (Ingredient ing : recipe.getIngredients()) {
                    String line;
                    try {
                        String original = ing.getOriginalString();
                        if (original != null && !original.isBlank()) {
                            line = original;
                        } else {
                            line = buildIngredientLine(ing);
                        }
                    } catch (NoSuchMethodError e) {
                        line = buildIngredientLine(ing);
                    }
                    ingredientLines.add(line);
                }
            }
            state.setIngredients(ingredientLines);

            // Steps
            List<String> stepLines = new ArrayList<>();
            if (recipe.getInstructionSteps() != null) {
                recipe.getInstructionSteps().stream()
                        .sorted(Comparator.comparing(InstructionStep::getStepNumber))
                        .forEach(step -> {
                            String desc = step.getDescription();
                            if (desc == null) desc = "";
                            stepLines.add(step.getStepNumber() + ". " + desc);
                        });
            }
            state.setSteps(stepLines);
        }

        state.setErrorMessage(null);
        viewModel.setState(state);

        // 🔴 Key difference: go to the NO-SAVE view
        viewManagerModel.setActiveViewName(SAVED_VIEW_NAME);
    }

    @Override
    public void presentFailure(String errorMessage) {
        ViewRecipeState state = new ViewRecipeState();

        state.setErrorMessage(errorMessage);
        state.setIngredients(new ArrayList<>());
        state.setSteps(new ArrayList<>());
        state.setCalories(null);
        state.setProtein(null);
        state.setFat(null);
        state.setCarbohydrates(null);
        state.setTitle(null);
        state.setSourceName(null);
        state.setSourceUrl(null);
        state.setServings(null);
        state.setReadyInMinutes(null);
        state.setImageUrl(null);

        viewModel.setState(state);

        // Show the no-save view even on error
        viewManagerModel.setActiveViewName(SAVED_VIEW_NAME);
    }

    private String buildIngredientLine(Ingredient ing) {
        StringBuilder sb = new StringBuilder();
        try {
            if (ing.getAmount() != null) {
                sb.append(ing.getAmount()).append(" ");
            }
        } catch (NoSuchMethodError ignored) {}
        try {
            if (ing.getUnit() != null && !ing.getUnit().isBlank()) {
                sb.append(ing.getUnit()).append(" ");
            }
        } catch (NoSuchMethodError ignored) {}
        try {
            if (ing.getName() != null) {
                sb.append(ing.getName());
            }
        } catch (NoSuchMethodError ignored) {}
        return sb.toString().trim();
    }
}
