package usecase.search_by_fridge;

/**
 * Input boundary for the Search By Fridge use case.
 */
public interface SearchByFridgeInputBoundary {

    /**
     * Execture the Search By Fridge use case.
     *
     * @param inputData the data required to perform the search,
     *                  including the user's ID and any additional
     *                  search paramter.
     */

    void execute(SearchByFridgeInputData inputData);
}
