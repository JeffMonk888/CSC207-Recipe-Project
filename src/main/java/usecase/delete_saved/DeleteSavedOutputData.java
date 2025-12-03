package usecase.delete_saved;

public class DeleteSavedOutputData {
    // Confirm which recipe was deleted
    private final String deletedRecipeKey;

    public DeleteSavedOutputData(String deletedRecipeKey) {
        this.deletedRecipeKey = deletedRecipeKey;
    }

    public String getDeletedRecipeKey() {
        return deletedRecipeKey;
    }
}
