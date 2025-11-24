package usecase.category;

public class CreateCategoryInteractor implements CreateCategoryInputBoundary {

    private final CategoryDataAccessInterface gateway;
    private final CreateCategoryOutputBoundary presenter;

    public CreateCategoryInteractor(CategoryDataAccessInterface gateway,
                                    CreateCategoryOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(CreateCategoryInputData inputData) {
        Long userId = inputData.getUserId();
        String name = inputData.getName() == null ? "" : inputData.getName().trim();

        if (name.isEmpty()) {
            presenter.presentFailure("Category name cannot be empty.");
            return;
        }

        if (gateway.categoryNameExists(userId, name)) {
            presenter.presentFailure("Category already exists.");
            return;
        }

        var category = gateway.createCategory(userId, name);
        presenter.presentSuccess(new CreateCategoryOutputData(category));
    }
}
