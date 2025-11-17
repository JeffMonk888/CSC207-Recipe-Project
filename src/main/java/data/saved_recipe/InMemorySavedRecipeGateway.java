package data.saved_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemorySavedRecipeGateway implements MotionForRecipe {

    private final Map<String, SavedRecipe> store = new HashMap<>();

    private final AtomicLong idCounter = new AtomicLong(1L);

    // Helper method to create a unique composite key for the map
    private String key(Long userId, Long recipeId) {
        return userId + "+" + recipeId;
    }

    @Override
    public boolean exists(Long userId, Long recipeId) {
        return store.containsKey(key(userId, recipeId));
    }

    @Override
    public void save(SavedRecipe newSave) {
        String k = key(newSave.getUserId(), newSave.getRecipeId());

        // Assign a new ID if the entity doesn't have one yet
        if (newSave.getId() == null) {
            newSave.setId(idCounter.getAndIncrement());
        }

        // Add or update the record in the map
        store.put(k, newSave);
    }

    @Override
    public ArrayList<SavedRecipe> findByUserId(Long userId) {
        return store.values().stream()
                .filter(savedRecipe -> Objects.equals(savedRecipe.getUserId(), userId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean delete(Long userId, Long recipeId) {
        SavedRecipe removed = store.remove(key(userId, recipeId));
        return removed != null;
    }
}
