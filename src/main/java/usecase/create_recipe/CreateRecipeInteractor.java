package usecase.create_recipe;

import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

public class CreateRecipeInteractor implements CreateRecipeInputBoundary {

    private final RecipeDataAssessObject recipeDAO;
    private final MotionForRecipe userRecipeDAO;
    private final CreateRecipeOutputBoundary presenter;

    public CreateRecipeInteractor(RecipeDataAssessObject recipeDAO,
                                  MotionForRecipe userRecipeDAO,
                                  CreateRecipeOutputBoundary presenter) {
        this.recipeDAO = recipeDAO;
        this.userRecipeDAO = userRecipeDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(CreateRecipeInputData inputData) {
        if (inputData.getTitle() == null || inputData.getTitle().trim().isEmpty()) {
            presenter.presentFailure("Title cannot be empty.");
            return;
        }

        // normal id
        long numericId = System.currentTimeMillis();

        // for SavedRecipe recipeKey and Recipe recipeid
        String recipeKey = "c" + numericId;

        // Create a Recipe
        Recipe newRecipe = new Recipe(
                numericId,
                inputData.getTitle(),
                "User created recipe",
                1, 0, "User", "", "",
                recipeKey,
                null
        );

        // add ingredient
        String ingStr = inputData.getIngredients();
        if (ingStr != null && !ingStr.isBlank()) {
            String[] parts = ingStr.split(",");
            for (String part : parts) {
                String name = part.trim();
                if (!name.isEmpty()) {
                    newRecipe.addIngredient(new Ingredient(null, name, null, "", name));
                }
            }
        }

        // add instruction
        String instStr = inputData.getInstructions();
        if (instStr != null && !instStr.isBlank()) {
            String[] lines = instStr.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                String step = lines[i].trim();
                if (!step.isEmpty()) {
                    newRecipe.addInstructionStep(new InstructionStep(null, i + 1, step));
                }
            }
        }

        // Save it to (recipe_cache.json)
        recipeDAO.save(newRecipe);

        // save it to (user_recipe_links.csv)
        SavedRecipe savedLink = new SavedRecipe(inputData.getUserId(), recipeKey);
        userRecipeDAO.save(savedLink);

        presenter.presentSuccess(new CreateRecipeOutputData(newRecipe.getTitle(), recipeKey));
    }
}
