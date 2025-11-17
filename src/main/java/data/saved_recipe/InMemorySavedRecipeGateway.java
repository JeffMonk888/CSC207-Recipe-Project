package data.saved_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the MotionforRecipe gateway.
 * Useful for development, testing, and running the app without a database.
 * Matches the style of InMemoryUserRatingGateway.
 */
public class InMemorySavedRecipeGateway implements MotionForRecipe {

    // Simulates a database table using a HashMap
    private final Map<String, SavedRecipe> store = new HashMap<>();

    // Simulates auto-incrementing primary keys for the 'id' field
    private final AtomicLong idCounter = new AtomicLong(1L);

    /**
     * Helper method to create a unique composite key for the map.
     */
    private String key(Long userId, Long recipeId) {
        return userId + ":" + recipeId;
    }

    @Override
    public boolean exists(Long userId, Long recipeId) {
        return store.containsKey(key(userId, recipeId));
    }

    /**
     * [MODIFIED] Saves the entity.
     * Because this is an in-memory store, we also assign a unique ID.
     */
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
        // Filter the map's values to find all records for the given user
        return store.values().stream()
                .filter(savedRecipe -> Objects.equals(savedRecipe.getUserId(), userId))
                .collect(Collectors.toCollection(ArrayList::new)); // Collect as ArrayList
    }

    @Override
    public boolean delete(Long userId, Long recipeId) {
        // remove() returns the value that was removed, or null if key didn't exist
        SavedRecipe removed = store.remove(key(userId, recipeId));
        return removed != null; // Return true if an item was successfully removed
    }
}
