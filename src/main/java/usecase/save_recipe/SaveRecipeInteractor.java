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
        Long recipeId = inputData.getRecipeId();

        // 1. Check alternate flow: Recipe already saved (from blueprint)
        if (motionForRecipeGateway.exists(userId, recipeId)) {
            presenter.presentFailure("Recipe is already in your collection.");
            return;
        }

        // 2. Create the domain entity (Interactor's responsibility)
        SavedRecipe newSave = new SavedRecipe(userId, recipeId);

        // 3. Call gateway to persist the entity.
        // The gateway implementation (e.g., InMemory) will set the ID on the 'newSave' object.
        motionForRecipeGateway.save(newSave);

        // 4. Prepare output data and notify the presenter of success
        SaveRecipeOutputData outputData = new SaveRecipeOutputData(newSave);
        presenter.presentSuccess(outputData);
    }
}
