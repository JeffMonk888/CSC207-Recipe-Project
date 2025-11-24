package interface_adapter.view_recipe;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {
    private final ViewRecipeInputBoundary interactor;

    public ViewRecipeController(ViewRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(long recipeId) {
        ViewRecipeInputData inputData = new ViewRecipeInputData(recipeId);
        interactor.execute(inputData);
    }
}
