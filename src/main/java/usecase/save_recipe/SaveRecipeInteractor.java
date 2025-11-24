package usecase.save_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

public class SaveRecipeInteractor implements SaveRecipeInputBoundary {

    private final MotionForRecipe motionForRecipeGateway;
    private final SaveRecipeOutputBoundary presenter;

    public SaveRecipeInteractor(MotionForRecipe motionForRecipeGateway,
                                SaveRecipeOutputBoundary presenter) {
        this.motionForRecipeGateway = motionForRecipeGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(SaveRecipeInputData inputData) {
        Long userId = inputData.getUserId();
        String recipeKey = inputData.getRecipeKey();

        if (motionForRecipeGateway.exists(userId, recipeKey)) {
            presenter.presentFailure("Recipe is already in your collection.");
            return;
        }

        SavedRecipe newSave = new SavedRecipe(userId, recipeKey);
        motionForRecipeGateway.save(newSave);

        SaveRecipeOutputData outputData = new SaveRecipeOutputData(newSave);
        presenter.presentSuccess(outputData);
    }
}
