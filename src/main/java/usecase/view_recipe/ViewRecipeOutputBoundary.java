package usecase.view_recipe;

/**
 * Output boundary for the View Recipe use case.
 */
public interface ViewRecipeOutputBoundary {
    /**
     * Presents the successful result of the View Recipe use case.
     *
     * @param outputData the data containing the complete recipe details, including ingredients,
     *                   instructions, nutrition, infromations, and image url etc.
     */
    void presentSuccess(ViewRecipeOutputData outputData);

    /**
     * Presents an error message when the recipe details cannot be retrieved.
     *
     * @param errorMessage an explanation of why the recipe could nto be displayed.
     *
     */
    void presentFailure(String errorMessage);
}
