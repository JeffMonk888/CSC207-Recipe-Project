package usecase.retrieve_saved;

import data.saved_recipe.RecipeDataAssessObject;
import data.api.SpoonacularClient;
import data.mapper.RecipeMapper;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.List;

public class RetrieveSavedInteractor implements RetrieveSavedInputBoundary {

    private final MotionForRecipe savedRecipeGateway;
    private final RecipeDataAssessObject recipeDataGateway;
    private final SpoonacularClient spoonacularClient;
    private final RetrieveSavedOutputBoundary presenter;

    public RetrieveSavedInteractor(MotionForRecipe savedRecipeGateway,
                                   RecipeDataAssessObject recipeDataGateway,
                                   SpoonacularClient spoonacularClient,
                                   RetrieveSavedOutputBoundary presenter) {
        this.savedRecipeGateway = savedRecipeGateway;
        this.recipeDataGateway = recipeDataGateway;
        this.spoonacularClient = spoonacularClient;
        this.presenter = presenter;
    }

    @Override
    public void execute(RetrieveSavedInputData inputData) {
        Long userId = inputData.getUserId();

        ArrayList<SavedRecipe> savedList = savedRecipeGateway.findByUserId(userId);

        List<Recipe> fullRecipes = new ArrayList<>();

        for (SavedRecipe saved : savedList) {

            Recipe recipe = null;

            if (saved.isCustomRecipe()) {
                long id = saved.getNumericRecipeId();   // "c1" -> 1
                recipe = recipeDataGateway.findById(id).orElse(null);

            } else if (saved.isApiRecipe()) {
                long apiId = saved.getNumericRecipeId();   // "a72345" -> 72345

                try {
                    var dto = spoonacularClient.getRecipeInformation(apiId, true);
                    recipe = RecipeMapper.toDomain(dto);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (recipe != null) {
                fullRecipes.add(recipe);
            } else {
                System.err.println("Warning: missing data for saved key: " + saved.getRecipeKey());
            }
        }

        // 3. Send list of full Recipes to presenter
        presenter.presentSuccess(new RetrieveSavedOutputData(fullRecipes));
    }
}
