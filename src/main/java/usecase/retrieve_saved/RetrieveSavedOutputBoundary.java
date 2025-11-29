package usecase.retrieve_saved;

// --- Output Boundary (Presenter) ---
public interface RetrieveSavedOutputBoundary {
    void presentSuccess(RetrieveSavedOutputData outputData);

    void presentFailure(String errorMessage);
}
