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
        ArrayList<SavedRecipe> recipes = motionForRecipeGateway.findByUserId(inputData.getUserId());

        RetrieveSavedOutputData outputData = new RetrieveSavedOutputData(recipes);
        presenter.presentSuccess(outputData);
    }
}
