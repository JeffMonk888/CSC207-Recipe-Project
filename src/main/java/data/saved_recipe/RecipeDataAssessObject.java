package data.saved_recipe;

import domain.entity.Recipe;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class RecipeDataAssessObject {

    private final File jsonFile;

    private final Map<Long, Recipe> recipeCache = new HashMap<>();

    public RecipeDataAssessObject(String jsonPath) {
        this.jsonFile = new File(jsonPath);

        if (jsonFile.length() == 0) {
            saveToFile();
        } else {
            loadFromFile();
        }
    }

    private void loadFromFile() {
        try {
            String content = new String(Files.readAllBytes(jsonFile.toPath()));
            JSONObject fileObject = new JSONObject(content);

            for (String key : fileObject.keySet()) {
                Long recipeId = Long.parseLong(key);
                JSONObject recipeJson = fileObject.getJSONObject(key);

                Recipe recipe = new Recipe(recipeJson);
                recipeCache.put(recipeId, recipe);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading recipe cache file", e);
        }
    }

    private void saveToFile() {
        JSONObject fileObject = new JSONObject();

        for (Map.Entry<Long, Recipe> entry : recipeCache.entrySet()) {
            fileObject.put(String.valueOf(entry.getKey()), entry.getValue().toJson());
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(fileObject.toString(4)); // toString(4) for pretty printing
        } catch (IOException e) {
            throw new RuntimeException("Error writing to recipe cache file", e);
        }
    }


    public void save(Recipe recipe) {
        recipeCache.put(recipe.getId(), recipe);
        this.saveToFile();
    }


    public Optional<Recipe> findById(Long recipeId) {
        return Optional.ofNullable(recipeCache.get(recipeId));
    }


    public boolean exists(Long recipeId) {
        return recipeCache.containsKey(recipeId);
    }
}