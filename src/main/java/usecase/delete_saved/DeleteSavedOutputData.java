package usecase.delete_saved;

public class DeleteSavedOutputData {
    private final String deletedRecipeKey; // Confirm which recipe was deleted

    public DeleteSavedOutputData(String deletedRecipeKey) {
        this.deletedRecipeKey = deletedRecipeKey;
    }
    public String getDeletedRecipeKey() { return deletedRecipeKey; }
}
