package usecase.category;

public class DeleteCategoryInteractor implements DeleteCategoryInputBoundary {

    private final CategoryDataAccessInterface gateway;
    private final DeleteCategoryOutputBoundary presenter;

    public DeleteCategoryInteractor(CategoryDataAccessInterface gateway,
                                    DeleteCategoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    public void execute(DeleteCategoryInputData inputData) {
        Long userId = inputData.getUserId();
        Long categoryId = inputData.getCategoryId();

        if (!gateway.categoryExistsForUser(userId, categoryId)) {
            presenter.presentFailure("Category not found for this user.");
            return;
        }

        gateway.deleteCategory(userId, categoryId);
        presenter.presentSuccess(new DeleteCategoryOutputData(categoryId));
    }
}
