package usecase.category.create_category;

import domain.entity.Category;

public class CreateCategoryOutputData {

    private final Category category;

    public CreateCategoryOutputData(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
