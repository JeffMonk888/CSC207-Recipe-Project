package interfaceadapter.search_by_fridge;

import domain.entity.RecipePreview;

import java.util.ArrayList;
import java.util.List;

public class SearchByFridgeState {

    private List<RecipePreview> recipes = new ArrayList<>();
    private int offset = 0;
    private boolean hasMore = false;
    private String errorMessage = null;

    public List<RecipePreview> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public void setRecipes(List<RecipePreview> recipes) {
        this.recipes = new ArrayList<>(recipes);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
