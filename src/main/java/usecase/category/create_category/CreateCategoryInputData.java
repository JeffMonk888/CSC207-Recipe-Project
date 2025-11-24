package usecase.category.create_category;

public class CreateCategoryInputData {

    private final Long userId;
    private final String name;

    public CreateCategoryInputData(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
}
