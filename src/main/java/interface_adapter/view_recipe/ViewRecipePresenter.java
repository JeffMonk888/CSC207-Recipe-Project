package interface_adapter.view_recipe;

import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.NutritionInfo;
import domain.entity.Recipe;
import interface_adapter.ViewManagerModel;
import usecase.view_recipe.ViewRecipeOutputBoundary;
import usecase.view_recipe.ViewRecipeOutputData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Presenter for UC5: View Recipe Details.
 *
 * Converts the domain Recipe in ViewRecipeOutputData into a ViewRecipeState
 * that the Swing View can easily render, and tells the ViewManagerModel
 * to switch to the View Recipe screen.
 */
public class ViewRecipePresenter implements ViewRecipeOutputBoundary {

    private final ViewRecipeViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewRecipePresenter(ViewRecipeViewModel viewModel,
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
                // If your Recipe class doesn't have getId(), just remove this.
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
                // calories is a Double; convert to something displayable
                if (n.getCalories() != null) {
                    state.setCalories(n.getCalories().toString());
                }

                state.setProtein(n.getProtein());         // e.g. "20g"
                state.setFat(n.getFat());                 // e.g. "15g"
                state.setCarbohydrates(n.getCarbohydrates()); // e.g. "30g"
            }

            // Ingredients -> List<String>
            List<String> ingredientLines = new ArrayList<>();
            if (recipe.getIngredients() != null) {
                for (Ingredient ing : recipe.getIngredients()) {
                    String line;

                    // Prefer originalString if you have it (human-friendly)
                    try {
                        String original = ing.getOriginalString();
                        if (original != null && !original.isBlank()) {
                            line = original;
                        } else {
                            line = buildIngredientLine(ing);
                        }
                    } catch (NoSuchMethodError e) {
                        // If Ingredient doesn't have getOriginalString(), fall back to formatting
                        line = buildIngredientLine(ing);
                    }

                    ingredientLines.add(line);
                }
            }
            state.setIngredients(ingredientLines);

            // Steps -> sorted List<String> by step number
            List<String> stepLines = new ArrayList<>();
            if (recipe.getInstructionSteps() != null) {
                recipe.getInstructionSteps().stream()
                        .sorted(Comparator.comparing(InstructionStep::getStepNumber))
                        .forEach(step -> {
                            String desc = step.getDescription();
                            if (desc == null) {
                                desc = "";
                            }
                            stepLines.add(step.getStepNumber() + ". " + desc);
                        });
            }
            state.setSteps(stepLines);
        }

        state.setErrorMessage(null);

        // Update ViewModel and notify the view
        viewModel.setState(state);

        // Switch to the View Recipe screen
        viewManagerModel.setActiveViewName(viewModel.getViewName());
    }

    @Override
    public void presentFailure(String errorMessage) {
        ViewRecipeState state = new ViewRecipeState();

        state.setErrorMessage(errorMessage);
        // Clear everything else on failure
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

        // Still go to the View Recipe screen so the user can see the error
        viewManagerModel.setActiveViewName(viewModel.getViewName());
    }

    /**
     * Helper to build a readable ingredient line from an Ingredient
     * if you don't have a good originalString.
     */
    private String buildIngredientLine(Ingredient ing) {
        StringBuilder sb = new StringBuilder();

        // amount
        try {
            if (ing.getAmount() != null) {
                sb.append(ing.getAmount());
                sb.append(" ");
            }
        } catch (NoSuchMethodError ignored) {
            // If there's no amount, just skip it.
        }

        // unit
        try {
            if (ing.getUnit() != null && !ing.getUnit().isBlank()) {
                sb.append(ing.getUnit()).append(" ");
            }
        } catch (NoSuchMethodError ignored) {}

        // name
        try {
            if (ing.getName() != null) {
                sb.append(ing.getName());
            }
        } catch (NoSuchMethodError ignored) {}

        return sb.toString().trim();
    }
}
