package usecase.delete_saved;

// --- Output Data ---
public class DeleteSavedOutputData {
    private final Long deletedRecipeId; // Confirm which recipe was deleted

    public DeleteSavedOutputData(Long deletedRecipeId) {
        this.deletedRecipeId = deletedRecipeId;
    }
    public Long getDeletedRecipeId() { return deletedRecipeId; }
}
