package interface_adapter.fridge;

import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInputData;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInputData;

public class FridgeController {

    private final AddIngredientInputBoundary addIngredientUseCase;
    private final RemoveIngredientInputBoundary removeIngredientUseCase;

    public FridgeController(AddIngredientInputBoundary addIngredientUseCase,
                            RemoveIngredientInputBoundary removeIngredientUseCase) {
        this.addIngredientUseCase = addIngredientUseCase;
        this.removeIngredientUseCase = removeIngredientUseCase;
    }

    public void addIngredient(Long userId, String ingredient) {
        AddIngredientInputData inputData =
                new AddIngredientInputData(userId, ingredient);
        addIngredientUseCase.execute(inputData);
    }

    public void removeIngredient(Long userId, String ingredient) {
        RemoveIngredientInputData inputData =
                new RemoveIngredientInputData(userId, ingredient);
        removeIngredientUseCase.execute(inputData);
    }
}
