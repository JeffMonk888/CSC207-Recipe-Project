package data.saved_recipe;

import domain.entity.SavedRecipe;
import domain.entity.UserRating;
import usecase.common.MotionForRecipe;
import usecase.rate_recipe.UserRatingDataAccessInterface;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Data access object for saved recipes backed by a CSV file.
 *
 * The CSV format is:
 *   id,userId,recipeId,savedAt,favourite,rating
 *
 * Important:
 *   - recipeId is treated as a STRING recipeKey (may contain letters).
 *   - rating is stored as a double string ("" means no rating).
 */
public class UserSavedRecipeAccessObject
        implements MotionForRecipe, UserRatingDataAccessInterface {

    private static final String HEADER_WITHOUT_RATING =
            "id,userId,recipeId,savedAt,favourite";
    private static final String HEADER_WITH_RATING =
            "id,userId,recipeId,savedAt,favourite,rating";

    private final File csvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>();

    // Saved recipes keyed by:  "userId:recipeKey"
    private final Map<String, SavedRecipe> savedRecipes = new HashMap<>();

    // Ratings keyed by:        "userId:recipeKey"
    private final Map<String, Double> ratings = new HashMap<>();

    private final AtomicLong idCounter = new AtomicLong(0);

    public UserSavedRecipeAccessObject(String path) {
        this.csvFile = new File(path);
        if (!csvFile.exists()) {
            createEmptyFile();
        } else {
            loadFromFile();
        }
    }

    /** Create a new empty CSV with full header. */
    private void createEmptyFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(HEADER_WITH_RATING);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error creating saved recipes file", e);
        }
        parseHeader(HEADER_WITH_RATING);
    }

    /** Parse header line and fill headers map. */
    private void parseHeader(String headerLine) {
        headers.clear();
        String[] cols = headerLine.split(",");
        for (int i = 0; i < cols.length; i++) {
            headers.put(cols[i].trim(), i);
        }
    }

    /** Load CSV records into memory. */
    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                createEmptyFile();
                return;
            }

            parseHeader(headerLine);
            boolean hasRating = headers.containsKey("rating");

            String line;
            long maxId = 0;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] values = line.split(",", -1); // keep empty columns

                Long id = Long.parseLong(values[headers.get("id")]);
                Long userId = Long.parseLong(values[headers.get("userId")]);

                // recipeId is STRING recipeKey (may contain letters)
                String recipeKey = values[headers.get("recipeId")].trim();

                String savedAtStr = values[headers.get("savedAt")];
                String favouriteStr = values[headers.get("favourite")];

                Instant savedAt =
                        savedAtStr.isEmpty() ? Instant.now() : Instant.parse(savedAtStr);
                boolean favourite = Boolean.parseBoolean(favouriteStr);

                String composite = compositeKey(userId, recipeKey);

                SavedRecipe saved = new SavedRecipe(userId, recipeKey);
                saved.setId(id);
                saved.setSavedAt(savedAt);
                saved.setFavourite(favourite);

                savedRecipes.put(composite, saved);

                if (hasRating) {
                    String ratingStr = values[headers.get("rating")].trim();
                    if (!ratingStr.isEmpty()) {
                        try {
                            ratings.put(composite, Double.parseDouble(ratingStr));
                        } catch (NumberFormatException ignored) {
                            // ignore malformed rating
                        }
                    }
                }

                if (id > maxId) maxId = id;
            }

            idCounter.set(maxId);

            // Upgrade file if old version missing rating column
            if (!headers.containsKey("rating")) {
                parseHeader(HEADER_WITH_RATING);
                saveToFile();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error loading saved recipes file", e);
        }
    }

    /** Build composite key: userId:recipeKey */
    private String compositeKey(Long userId, String recipeKey) {
        return userId + ":" + recipeKey;
    }

    /** Write all saved recipes + ratings to CSV. */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {

            writer.write(HEADER_WITH_RATING);
            writer.newLine();

            for (SavedRecipe saved : savedRecipes.values()) {

                Long id = saved.getId();
                if (id == null) {
                    id = idCounter.incrementAndGet();
                    saved.setId(id);
                }

                Long userId = saved.getUserId();
                String recipeKey = saved.getRecipeKey();  // store as STRING

                Instant savedAt = saved.getSavedAt();
                if (savedAt == null) {
                    savedAt = Instant.now();
                    saved.setSavedAt(savedAt);
                }

                boolean favourite = saved.isFavourite();
                Double rating = ratings.get(compositeKey(userId, recipeKey));
                String ratingStr = rating == null ? "" : rating.toString();

                String line = String.join(",",
                        id.toString(),
                        userId.toString(),
                        recipeKey,
                        savedAt.toString(),
                        Boolean.toString(favourite),
                        ratingStr
                );

                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving recipes to file", e);
        }
    }

    // =================================
    // MotionForRecipe Implementation
    // =================================

    @Override
    public boolean exists(Long userId, String recipeKey) {
        return savedRecipes.containsKey(compositeKey(userId, recipeKey));
    }

    @Override
    public void save(SavedRecipe newSave) {
        String key = compositeKey(newSave.getUserId(), newSave.getRecipeKey());

        if (newSave.getId() == null) {
            newSave.setId(idCounter.incrementAndGet());
        }
        if (newSave.getSavedAt() == null) {
            newSave.setSavedAt(Instant.now());
        }

        savedRecipes.put(key, newSave);
        saveToFile();
    }

    @Override
    public ArrayList<SavedRecipe> findByUserId(Long userId) {
        return savedRecipes.values().stream()
                .filter(sr -> Objects.equals(sr.getUserId(), userId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean delete(Long userId, String recipeKey) {
        String key = compositeKey(userId, recipeKey);
        SavedRecipe removed = savedRecipes.remove(key);
        ratings.remove(key);

        if (removed != null) {
            saveToFile();
            return true;
        }
        return false;
    }

    // =================================
    // UserRatingDataAccessInterface Impl
    // =================================

    /**
     * UC9 uses numeric recipeId, but our storage uses STRING recipeKey.
     * We convert using String.valueOf(recipeId).
     */
    @Override
    public UserRating findByUserAndRecipe(long userId, long recipeId) {
        String recipeKey = String.valueOf(recipeId);
        String key = compositeKey(userId, recipeKey);
        Double stars = ratings.get(key);

        if (stars == null) return null;
        return new UserRating(userId, recipeId, stars);
    }

    @Override
    public void save(UserRating rating) {
        long userId = rating.getUserId();
        long recipeId = rating.getRecipeId();
        String recipeKey = String.valueOf(recipeId);

        String key = compositeKey(userId, recipeKey);

        // Ensure recipe exists
        if (!savedRecipes.containsKey(key)) {
            SavedRecipe newSave = new SavedRecipe(userId, recipeKey);
            newSave.setFavourite(false);
            newSave.setSavedAt(Instant.now());
            save(newSave);
        }

        ratings.put(key, rating.getStars());
        saveToFile();
    }

    @Override
    public void deleteRating(long userId, long recipeId) {
        String recipeKey = String.valueOf(recipeId);
        String key = compositeKey(userId, recipeKey);
        if (ratings.remove(key) != null) {
            saveToFile();
        }
    }
}
