package interface_adapter.category;

import java.util.ArrayList;
import java.util.List;

/**
 * View state for the category-related use cases.
 *
 * This state is held by {@link CategoryViewModel} and observed by the GUI.
 * It stores the current category list, the filtered recipes for a selected
 * category, and any messages or errors that should be displayed.
 */
public class CategoryState {

    /**
     * Human-readable representations of the user's categories.
     * For example: "1 - Dinner".
     */
    private List<String> categories = new ArrayList<>();

    /**
     * Human-readable representations of recipes that belong to the
     * currently selected category.
     */
    private List<String> filteredRecipes = new ArrayList<>();

    /**
     * Id of the currently selected category, if any.
     */
    private Long selectedCategoryId;

    /**
     * True if the bottom list is currently filtered by a category.
     */
    private boolean filtered;

    /**
     * Informational message (for success cases).
     */
    private String infoMessage;

    /**
     * Error message (for failure cases).
     */
    private String errorMessage;

    public CategoryState() {
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    public void setCategories(List<String> categories) {
        this.categories = new ArrayList<>(categories);
    }

    public List<String> getFilteredRecipes() {
        return new ArrayList<>(filteredRecipes);
    }

    public void setFilteredRecipes(List<String> filteredRecipes) {
        this.filteredRecipes = new ArrayList<>(filteredRecipes);
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
