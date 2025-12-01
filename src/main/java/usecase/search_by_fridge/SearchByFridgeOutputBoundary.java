package usecase.search_by_fridge;

/**
 * Output Boudnary for the Search By Fridge use case.
 *
 */
public interface SearchByFridgeOutputBoundary {
    /**
     * Present the sucessful result of the Search By Fridge use case.
     *
     * @param outputData the data containing the filtered recipe results,
     *                   page info, and any additional presentation details
     *                   required by the UI
     */
    void presentSuccess(SearchByFridgeOutputData outputData);

    /**
     * Present the failure message when the Search By Fridge use case cannot complete successfully.
     *
     * @param errorMessage a related description of why the search failed
     */
    void presentFailure(String errorMessage);
}
