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
        String recipeKey = inputData.getRecipeKey();

        if (!motionForRecipe.exists(userId, recipeKey)) {
            presenter.presentFailure("Recipe not found in collection.");
            return;
        }

        boolean success = motionForRecipe.delete(userId, recipeKey);

        if (success) {
            DeleteSavedOutputData outputData = new DeleteSavedOutputData(recipeKey);
            presenter.presentSuccess(outputData);
        } else {
            presenter.presentFailure("Failed to delete recipe.");
        }
    }
}
