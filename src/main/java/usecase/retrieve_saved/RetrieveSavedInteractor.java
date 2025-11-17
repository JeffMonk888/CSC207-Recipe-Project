package usecase.retrieve_saved;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

import java.util.ArrayList;

public class RetrieveSavedInteractor implements RetrieveSavedInputBoundary {

    private final MotionForRecipe motionForRecipeGateway;
    private final RetrieveSavedOutputBoundary presenter;

    public RetrieveSavedInteractor(MotionForRecipe motionForRecipeGateway,
                                   RetrieveSavedOutputBoundary presenter) {
        this.motionForRecipeGateway = motionForRecipeGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(RetrieveSavedInputData inputData) {
        // 1. Fetch data from the gateway
        ArrayList<SavedRecipe> recipes = motionForRecipeGateway.findByUserId(inputData.getUserId());

        // 2. Pass the list (even if empty) to the presenter
        // The "No recipes found" case is handled by the UI
        // by checking if the list is empty.
        RetrieveSavedOutputData outputData = new RetrieveSavedOutputData(recipes);
        presenter.presentSuccess(outputData);
    }
}
