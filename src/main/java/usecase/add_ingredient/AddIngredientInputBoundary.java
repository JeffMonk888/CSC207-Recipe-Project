package usecase.add_ingredient;

/**
 * Input boundary for the Add Ingredient use case.
 */
public interface AddIngredientInputBoundary {
    /**
     * Executes the Add Ingredient use case.
     *
     * @param inputData the data required to add ingredient, including the user ID and the ingredient name
     */
    void execute(AddIngredientInputData inputData);
}
