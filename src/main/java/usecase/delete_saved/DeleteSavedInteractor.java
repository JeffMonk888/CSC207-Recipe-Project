package usecase.delete_saved;

import usecase.common.MotionForRecipe;

public class DeleteSavedInteractor implements DeleteSavedInputBoundary {

    private final MotionForRecipe motionForRecipe;
    private final DeleteSavedOutputBoundary presenter;

    public DeleteSavedInteractor(MotionForRecipe motionForRecipe,
                                 DeleteSavedOutputBoundary presenter) {
        this.motionForRecipe = motionForRecipe;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeleteSavedInputData inputData) {
        Long userId = inputData.getUserId();
        Long recipeId = inputData.getRecipeId();

        // 1. Check if the recipe actually exists in the collection
        if (!motionForRecipe.exists(userId, recipeId)) {
            presenter.presentFailure("Recipe not found in collection.");
            return;
        }

        // 2. Perform the deletion
        // (Confirmation prompts are handled by the UI before calling execute)
        boolean success = motionForRecipe.delete(userId, recipeId);

        // 3. Notify the presenter
        if (success) {
            DeleteSavedOutputData outputData = new DeleteSavedOutputData(recipeId);
            presenter.presentSuccess(outputData);
        } else {
            // This case might happen in a real DB, e.g., connection lost
            presenter.presentFailure("Failed to delete recipe.");
        }
    }
}
