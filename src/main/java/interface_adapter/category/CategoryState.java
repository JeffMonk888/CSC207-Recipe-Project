package interface_adapter.category;

import domain.entity.Category;
import domain.entity.SavedRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * State object for UC10: Category management.
 *
 * This is what the CategoryViewModel exposes to the Swing view.
 * The view should observe this state (via PropertyChange events)
 * and redraw its components when the state changes.
 */
public class CategoryState {

    /** All categories that belong to the current user. */
    private List<Category> categories = new ArrayList<>();

    /** Saved recipes that belong to the currently selected / filtered category. */
    private List<SavedRecipe> filteredRecipes = new ArrayList<>();

    /**
     * The list of recipe IDs that were most recently assigned to a category.
     * This is useful for the view to show feedback after an "assign" action.
     */
    private List<String> assignedRecipeIds = new ArrayList<>();

    /** The ID of the currently selected category in the UI (may be null). */
    private Long selectedCategoryId;

    /** User-facing info / success message. */
    private String message;

    /** User-facing error message (null if no error). */
    private String errorMessage;

    // ===== Categories =====

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories = (categories == null)
                ? new ArrayList<>()
                : new ArrayList<>(categories);
    }

    // ===== Filtered recipes =====

    public List<SavedRecipe> getFilteredRecipes() {
        return new ArrayList<>(filteredRecipes);
    }

    public void setFilteredRecipes(List<SavedRecipe> filteredRecipes) {
        this.filteredRecipes = (filteredRecipes == null)
                ? new ArrayList<>()
                : new ArrayList<>(filteredRecipes);
    }

    // ===== Assigned recipe IDs (for last assign action) =====

    public List<String> getAssignedRecipeIds() {
        return new ArrayList<>(assignedRecipeIds);
    }

    public void setAssignedRecipeIds(List<String> assignedRecipeIds) {
        this.assignedRecipeIds = (assignedRecipeIds == null)
                ? new ArrayList<>()
                : new ArrayList<>(assignedRecipeIds);
    }

    // ===== Selected category =====

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    // ===== Messages =====

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
