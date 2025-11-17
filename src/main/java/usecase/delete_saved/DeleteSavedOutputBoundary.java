package usecase.delete_saved;

public interface DeleteSavedOutputBoundary {
    void presentSuccess(DeleteSavedOutputData outputData);
    void presentFailure(String errorMessage);
}
