package usecase.add_ingredient;

/**
 * Output boundary for the Add Ingredient usecase.
 */
public interface AddIngredientOutputBoundary {

    /**
     * Presents the successful result of the Add Ingredient use case.
     *
     * @param outputData the data object containing the user ID
     *                  and the ingredient that was successfully added
     */
    void presentSuccess(AddIngredientOutputData outputData);

    /**
     * Presents an error message when the ingredient cannot be added.
     *
     * @param errorMessage an understable explanation of why the operation failed
     */
    void presentFailure(String errorMessage);
}
