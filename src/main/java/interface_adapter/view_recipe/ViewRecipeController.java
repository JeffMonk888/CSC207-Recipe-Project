package interface_adapter.view_recipe;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInputData;

public class ViewRecipeController {
    private final ViewRecipeInputBoundary interactor;

    public ViewRecipeController(ViewRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String recipeKey) {
        ViewRecipeInputData inputData = new ViewRecipeInputData(recipeKey);
        interactor.execute(inputData);
    }
}
