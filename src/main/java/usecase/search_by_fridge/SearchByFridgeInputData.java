package usecase.search_by_fridge;

public class SearchByFridgeInputData {

    private final Long userId;
    private final int number;
    private final int offset;

    public SearchByFridgeInputData(Long userId, int number, int offset) {
        this.userId = userId;
        this.number = number;
        this.offset = offset;
    }

    public Long getUserId() {
        return userId;
    }

    public int getNumber() {
        return number;
    }

    public int getOffset() {
        return offset;
    }
}
