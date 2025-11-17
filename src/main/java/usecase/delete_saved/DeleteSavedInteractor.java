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

        if (!motionForRecipe.exists(userId, recipeId)) {
            presenter.presentFailure("Recipe not found in collection.");
            return;
        }

        boolean success = motionForRecipe.delete(userId, recipeId);

        if (success) {
            DeleteSavedOutputData outputData = new DeleteSavedOutputData(recipeId);
            presenter.presentSuccess(outputData);
        } else {
            presenter.presentFailure("Failed to delete recipe.");
        }
    }
}
