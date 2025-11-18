package data.saved_recipe;

import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe; // 您的接口

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


public class UserSavedRecipeAccessObject implements MotionForRecipe {

    private static final String HEADER = "id,userId,recipeId,savedAt,favourite";
    private final File csvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>(); // for data from the file

    // In-memory cache of all save records, loaded from the file.
    private final Map<String, SavedRecipe> savedRecipes = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0L);

    private String key(Long userId, Long recipeId) {
        return userId + ":" + recipeId;
    }


    public UserSavedRecipeAccessObject(String csvPath) {
        this.csvFile = new File(csvPath);
        headers.put("id", 0);
        headers.put("userId", 1);
        headers.put("recipeId", 2);
        headers.put("savedAt", 3);
        headers.put("favourite", 4);

        if (csvFile.length() == 0) {
            saveToFile(); // Write header if file is new
        } else {
            loadFromFile(); // Load existing data into memory
        }
    }


    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String header = reader.readLine();
            if (!header.equals(HEADER)) {
                throw new RuntimeException("File header does not match expected: " + HEADER);
            }

            long maxId = 0L;
            String row;
            while ((row = reader.readLine()) != null) {
                String[] col = row.split(",");
                Long id = Long.parseLong(col[headers.get("id")]);
                Long userId = Long.parseLong(col[headers.get("userId")]);
                Long recipeId = Long.parseLong(col[headers.get("recipeId")]);
                Instant savedAt = Instant.parse(col[headers.get("savedAt")]);
                boolean favourite = Boolean.parseBoolean(col[headers.get("favourite")]);

                SavedRecipe recipe = new SavedRecipe(userId, recipeId);
                recipe.setId(id);
                recipe.setSavedAt(savedAt);
                recipe.setFavourite(favourite);

                savedRecipes.put(key(userId, recipeId), recipe);
                maxId = Math.max(maxId, id);
            }
            idCounter.set(maxId);
        } catch (IOException e) {
            throw new RuntimeException("Error reading saved recipes file", e);
        }
    }


    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(String.join(",", headers.keySet()));
            writer.newLine();

            for (SavedRecipe recipe : savedRecipes.values()) {
                String line = String.format("%s,%s,%s,%s,%s",
                        recipe.getId(), recipe.getUserId(), recipe.getRecipeId(),
                        recipe.getSavedAt().toString(), recipe.isFavourite()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to saved recipes file", e);
        }
    }

    @Override
    public boolean exists(Long userId, Long recipeId) {
        return savedRecipes.containsKey(key(userId, recipeId));
    }

    @Override
    public void save(SavedRecipe newSave) {
        if (newSave.getId() == null) {
            newSave.setId(idCounter.incrementAndGet());
        }
        savedRecipes.put(key(newSave.getUserId(), newSave.getRecipeId()), newSave);
        this.saveToFile();
    }

    @Override
    public ArrayList<SavedRecipe> findByUserId(Long userId) {
        return savedRecipes.values().stream()
                .filter(recipe -> Objects.equals(recipe.getUserId(), userId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean delete(Long userId, Long recipeId) {
        SavedRecipe removed = savedRecipes.remove(key(userId, recipeId));
        if (removed != null) {
            this.saveToFile(); // Persist change
            return true;
        }
        return false;
    }
}
