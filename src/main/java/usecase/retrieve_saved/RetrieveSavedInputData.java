package usecase.retrieve_saved;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;
import java.util.ArrayList;


public class RetrieveSavedInputData {
    private final Long userId;

    public RetrieveSavedInputData(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}


