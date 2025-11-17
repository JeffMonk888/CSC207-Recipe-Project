package usecase.save_recipe;

// --- Output Boundary (The Presenter Interface) ---
public interface SaveRecipeOutputBoundary {
    void presentSuccess(SaveRecipeOutputData outputData);
    void presentFailure(String errorMessage);
}
