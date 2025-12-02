package usecase.remove_ingredient;

/**
 * Input boundary for the Remove Ingredient use case.
 */
public interface RemoveIngredientInputBoundary {
    /**
     * Executes the Remove Ingredient use case.
     *
     * @param inputData the data required to attempt removal
     */
    void execute(RemoveIngredientInputData inputData);
}
