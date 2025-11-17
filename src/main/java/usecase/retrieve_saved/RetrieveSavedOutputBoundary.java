package usecase.retrieve_saved;

// --- Output Boundary (Presenter) ---
public interface RetrieveSavedOutputBoundary {
    void presentSuccess(RetrieveSavedOutputData outputData);
    // Note: No 'presentFailure' as returning an empty list is considered a "success"
}
