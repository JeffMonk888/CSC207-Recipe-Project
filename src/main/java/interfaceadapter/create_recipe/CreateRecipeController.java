package interfaceadapter.create_recipe;

import usecase.create_recipe.CreateRecipeInputBoundary;
import usecase.create_recipe.CreateRecipeInputData;

public class CreateRecipeController {
    private final CreateRecipeInputBoundary interactor;

    public CreateRecipeController(CreateRecipeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(Long userId, String title, String ingredients, String instructions) {
        CreateRecipeInputData data = new CreateRecipeInputData(userId, title, ingredients, instructions);
        interactor.execute(data);
    }
}
