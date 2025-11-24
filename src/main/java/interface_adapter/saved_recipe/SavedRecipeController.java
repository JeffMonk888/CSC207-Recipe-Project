package interface_adapter.saved_recipe;

import usecase.delete_saved.DeleteSavedInputBoundary;
import usecase.delete_saved.DeleteSavedInputData;
import usecase.retrieve_saved.RetrieveSavedInputBoundary;
import usecase.retrieve_saved.RetrieveSavedInputData;
import usecase.save_recipe.SaveRecipeInputBoundary;
import usecase.save_recipe.SaveRecipeInputData;

public class SavedRecipeController {

    private final RetrieveSavedInputBoundary retrieveUseCase;
    private final DeleteSavedInputBoundary deleteUseCase;

    public SavedRecipeController(RetrieveSavedInputBoundary retrieveUseCase,
                                 DeleteSavedInputBoundary deleteUseCase) {
        this.retrieveUseCase = retrieveUseCase;
        this.deleteUseCase = deleteUseCase;
    }

    public void executeRetrieve(Long userId) {
        RetrieveSavedInputData data = new RetrieveSavedInputData(userId);
        retrieveUseCase.execute(data);
    }

    public void executeDelete(Long userId, String recipeKey) {
        DeleteSavedInputData data = new DeleteSavedInputData(userId, recipeKey);
        deleteUseCase.execute(data);
    }
}


