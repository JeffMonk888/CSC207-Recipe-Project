package usecase.remove_ingredient;

/**
 * Output boundary for the Remove Ingredient use case.
 */
public interface RemoveIngredientOutputBoundary {
    /**
     * Reports that the ingredient was successfully removed.
     *
     * @param outputData the data containing information about the
     *                   ingredient that was removed
     */
    void presentSuccess(RemoveIngredientOutputData outputData);

    /**
     * Reports that the ingredient removal failed.
     *
     * @param errorMessage a description of why the removal failed
     */
    void presentFailure(String errorMessage);
}
