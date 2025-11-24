package data.api;

import data.dto.RecipeInformationDTO;
import domain.entity.Ingredient;
import domain.entity.RecipePreview;
import jdk.jshell.spi.SPIResolutionException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import usecase.common.RecipeByIngredientsAccess;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//for UC5: View domain.entity.Recipe Details
public class SpoonacularClient implements RecipeByIngredientsAccess {

    private static final String BASE = "https://api.spoonacular.com";
    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(15))
            .build();
    private final String apiKey;

    public SpoonacularClient(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey required");
    }

    // Create the DTO
    public RecipeInformationDTO getRecipeInformation(long id, boolean includeNutrition) throws ApiException {

        RecipeInformationDTO dto = new RecipeInformationDTO();

        fillBasicAndIngredients(id, dto);
        fillInstruction(id, dto);

        if (includeNutrition) {
            fillNutrition(id, dto);
        }
        return dto;
    }

    @Override
    public List<RecipePreview> getRecipesForIngredients(
            List<String> ingredientList,
            int number,
            int offset
    ) throws ApiException {
        StringBuilder ingredients = new StringBuilder();
        for (String ingredient : ingredientList) {
            if (ingredient.length() > 0) {
                ingredients.append(",");
            }
            ingredients.append(ingredient);
        }
        String url = String.format(
                "%s/recipes/findByIngredients?ingredients=%s&number=%d&offset=%d&ranking=2&apiKey=%s",
                BASE, ingredients, number, offset, apiKey
        );

        JSONArray jsonArray = getJsonArray(url);

        ArrayList<RecipePreview> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            RecipePreview recipePreview = new RecipePreview();
            recipePreview.id = jsonObject.getLong("id");
            recipePreview.image = jsonObject.optString("image");
            recipePreview.imageType = jsonObject.optString("imageType");
            recipePreview.likes = jsonObject.optInt("likes");
            recipePreview.title = jsonObject.optString("title");
            recipePreview.missedIngredientCount = jsonObject.getInt("missedIngredientCount");
            recipes.add(recipePreview);
        }
        return recipes;
    }


    private void fillBasicAndIngredients(long id, RecipeInformationDTO dto) throws ApiException {
        String url = BASE + "/recipes/" + id + "/information?apiKey=" + apiKey;

        JSONObject root = getJson(url);

        dto.id = root.optLong("id");
        dto.title = root.optString("title", "");
        dto.image = root.optString("image", "");
        dto.servings = root.has("servings") ? root.optInt("servings") : null;
        dto.readyInMinutes = root.has("readyInMinutes") ? root.optInt("readyInMinutes") : null;
        dto.sourceName = root.optString("sourceName", "");
        dto.sourceUrl = root.optString("sourceUrl", "");

        // ingredients required
        JSONArray ings = root.optJSONArray("extendedIngredients");
        if (ings != null) {
            for (int i = 0; i < ings.length(); i++) {
                JSONObject ingredientJson = ings.getJSONObject(i);

                RecipeInformationDTO.ExtendedIngredient ingredientDTO =
                        new RecipeInformationDTO.ExtendedIngredient();
                ingredientDTO.name = ingredientJson.optString("name", "");
                ingredientDTO.amount = ingredientJson.has("amount")
                        ? ingredientJson.optDouble("amount")
                        : null;
                ingredientDTO.unit = ingredientJson.optString("unit", "");

                dto.ingredients.add(ingredientDTO);
            }
        }

    }

    private void fillInstruction(long id, RecipeInformationDTO dto) throws ApiException {
        String url = BASE + "/recipes/" + id + "/analyzedInstructions?apiKey=" + apiKey;
        JSONArray blocks = getJsonArray(url);
        if (blocks != null && !blocks.isEmpty()) {
            JSONObject instructionBlock = blocks.getJSONObject(0);
            JSONArray stepsArray = instructionBlock.optJSONArray("steps");
            if (stepsArray == null) return;

            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepJson = stepsArray.getJSONObject(i);

                RecipeInformationDTO.Step stepDTO = new RecipeInformationDTO.Step();
                stepDTO.number = stepJson.optInt("number", i + 1);
                stepDTO.step = stepJson.optString("step", "");

                dto.steps.add(stepDTO);
            }
        }

    }

    private void fillNutrition(long id, RecipeInformationDTO dto) throws ApiException {
        String url = BASE + "/recipes/" + id + "/nutritionWidget.json?apiKey=" + apiKey;
        JSONObject root = getJson(url);

        JSONArray nutrientsArray = root.optJSONArray("nutrients");
        if (nutrientsArray == null) return;

        for (int i = 0; i < nutrientsArray.length(); i++) {
            JSONObject nutrientJson = nutrientsArray.getJSONObject(i);

            String name = nutrientJson.optString("name", "").toLowerCase();
            Double amount = nutrientJson.has("amount")
                    ? nutrientJson.optDouble("amount")
                    : null;
            String unit = nutrientJson.optString("unit", "");

            switch (name) {
                case "calories" -> dto.calories = amount;
                case "protein" -> {
                    dto.proteinAmount = amount;
                    dto.proteinUnit = unit;
                }
                case "fat" -> {
                    dto.fatAmount = amount;
                    dto.fatUnit = unit;
                }
                case "carbohydrates" -> {
                    dto.carbsAmount = amount;
                    dto.carbsUnit = unit;
                }
            }
        }
    }

    private JSONObject getJson(String url) throws ApiException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = http.newCall(request).execute()) {
            int code =  response.code();
            String body = response.body() != null ? response.body().string() : "";

            if (code < 200 || code >= 300) {
                String msg = switch (code) {
                    case 401 -> "Unauthorized (check API key).";
                    case 402, 429 -> "Quota / rate limit exceeded.";
                    case 404 -> "Recipe not found.";
                    default -> "HTTP error " + code;
                };
                throw new ApiException(msg, code, body);
            }
            return new JSONObject(body);
        } catch (IOException e) {
            throw new ApiException("IO error calling " + url, e);
        }
    }

    private JSONArray getJsonArray(String url) throws ApiException {
        Request req = new Request.Builder().url(url).get().build();

        try (Response resp = http.newCall(req).execute()) {
            int code = resp.code();
            String body = resp.body() != null ? resp.body().string() : "";

            if (code < 200 || code >= 300) {
                String msg = switch (code) {
                    case 401 -> "Unauthorized (check API key).";
                    case 402, 429 -> "Quota / rate limit exceeded.";
                    case 404 -> "Recipe not found.";
                    default -> "HTTP error " + code;
                };
                throw new ApiException(msg, code, body);
            }

            return new JSONArray(body);
        } catch (IOException e) {
            throw new ApiException("Network/IO error: " + e.getMessage(), e);
        }
    }
    // error wrapper
    public static class ApiException extends Exception {
        public final int statusCode;
        public final String body;
        public ApiException(String message, int statusCode, String body) {
            super(message);
            this.statusCode = statusCode;
            this.body = body;
        }
        public ApiException(String message, Throwable cause) {
            super(message, cause);
            this.statusCode = -1;
            this.body = null;
        }
    }


}
