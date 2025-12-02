package usecase.search_by_fridge;

import java.util.List;

import domain.entity.RecipePreview;

public class SearchByFridgeOutputData {

    private final List<RecipePreview> recipes;
    private final int nextOffset;
    private final boolean hasMore;

    public SearchByFridgeOutputData(List<RecipePreview> recipes, int nextOffset, boolean hasMore) {
        this.recipes = recipes;
        this.nextOffset = nextOffset;
        this.hasMore = hasMore;
    }

    public List<RecipePreview> getRecipes() {
        return recipes;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public boolean hasMore() {
        return hasMore;
    }
}
