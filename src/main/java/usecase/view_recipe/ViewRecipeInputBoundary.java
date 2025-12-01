package usecase.view_recipe;

/**
 * Input boundary for the View Recipe use case
 */
public interface ViewRecipeInputBoundary {
    /**
     * Executes the View Recipe use case.
     *
     * @param inputData the data needed to retrieve and display a recipe's
     *                  full detail.
     */
    void execute(ViewRecipeInputData inputData);
}

