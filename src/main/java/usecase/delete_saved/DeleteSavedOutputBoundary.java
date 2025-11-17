package usecase.delete_saved;

// --- Output Boundary (Presenter) ---
public interface DeleteSavedOutputBoundary {
    void presentSuccess(DeleteSavedOutputData outputData);
    void presentFailure(String errorMessage);
}
