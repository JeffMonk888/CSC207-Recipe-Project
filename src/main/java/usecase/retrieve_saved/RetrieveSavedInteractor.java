package usecase.retrieve_saved;

import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RetrieveSavedInteractor implements RetrieveSavedInputBoundary {

    private final MotionForRecipe savedRecipeGateway;
    private final RecipeDataAssessObject recipeDataGateway;
    private final RetrieveSavedOutputBoundary presenter;


    public RetrieveSavedInteractor(MotionForRecipe savedRecipeGateway,
                                   RecipeDataAssessObject recipeDataGateway,
                                   RetrieveSavedOutputBoundary presenter) {
        this.savedRecipeGateway = savedRecipeGateway;
        this.recipeDataGateway = recipeDataGateway;
        this.presenter = presenter;
    }


    @Override
    public void execute(RetrieveSavedInputData inputData) {

        List<SavedRecipe> links = savedRecipeGateway.findByUserId(inputData.getUserId());

        List<Recipe> fullRecipes = new ArrayList<>();

        for (SavedRecipe link : links) {
            Long recipeId = link.getRecipeId();

            Optional<Recipe> recipeOpt = recipeDataGateway.findById(recipeId);

            if (recipeOpt.isPresent()) {
                fullRecipes.add(recipeOpt.get());
            } else {

                System.err.println("Warning: Data missing for saved recipe ID: " + recipeId);
            }
        }

        presenter.presentSuccess(new RetrieveSavedOutputData(fullRecipes));
    }
}
