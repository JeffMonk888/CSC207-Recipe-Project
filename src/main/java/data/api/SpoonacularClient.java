package data.api;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import data.dto.RecipeInformationDto;
import domain.entity.RecipePreview;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import usecase.common.RecipeByIngredientsAccess;

// for UC5: View domain.entity.Recipe Details
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
    public RecipeInformationDto getRecipeInformation(long id, boolean includeNutrition) throws ApiException {

        RecipeInformationDto dto = new RecipeInformationDto();

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

        final StringBuilder ingredients = new StringBuilder();
        for (String ingredient : ingredientList) {
            if (!ingredient.isEmpty()) {
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

            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            final RecipePreview recipePreview = new RecipePreview();
            recipePreview.id = jsonObject.getLong("id");
            recipePreview.recipeKey = "a" + jsonObject.getLong("id");
            recipePreview.image = jsonObject.optString("image");
            recipePreview.imageType = jsonObject.optString("imageType");
            recipePreview.likes = jsonObject.optInt("likes");
            recipePreview.title = jsonObject.optString("title");
            recipePreview.missedIngredientCount = jsonObject.getInt("missedIngredientCount");

            recipes.add(recipePreview);
        }
        return recipes;
    }


    private void fillBasicAndIngredients(long id, RecipeInformationDto dto) throws ApiException {
        final String url = BASE + "/recipes/" + id + "/information?apiKey=" + apiKey;

        final JSONObject root = getJson(url);

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
                final JSONObject ingredientJson = ings.getJSONObject(i);

                final RecipeInformationDto.ExtendedIngredient ingredientdto =
                        new RecipeInformationDto.ExtendedIngredient();
                ingredientdto.name = ingredientJson.optString("name", "");
                ingredientdto.amount = ingredientJson.has("amount")
                        ? ingredientJson.optDouble("amount")
                        : null;
                ingredientdto.unit = ingredientJson.optString("unit", "");

                dto.ingredients.add(ingredientdto);
            }
        }

    }

    private void fillInstruction(long id, RecipeInformationDto dto) throws ApiException {
        String url = BASE + "/recipes/" + id + "/analyzedInstructions?apiKey=" + apiKey;
        JSONArray blocks = getJsonArray(url);
        if (!blocks.isEmpty()) {
            final JSONObject instructionBlock = blocks.getJSONObject(0);
            final JSONArray stepsArray = instructionBlock.optJSONArray("steps");
            if (stepsArray != null) {
                for (int i = 0; i < stepsArray.length(); i++) {
                    final JSONObject stepJson = stepsArray.getJSONObject(i);

                    final RecipeInformationDto.Step stepdto = new RecipeInformationDto.Step();
                    stepdto.number = stepJson.optInt("number", i + 1);
                    stepdto.step = stepJson.optString("step", "");

                    dto.steps.add(stepdto);
                }
            }
        }

    }

    private void fillNutrition(long id, RecipeInformationDto dto) throws ApiException {
        final String url = BASE + "/recipes/" + id + "/nutritionWidget.json?apiKey=" + apiKey;
        final JSONObject root = getJson(url);

        final JSONArray nutrientsArray = root.optJSONArray("nutrients");
        if (nutrientsArray != null) {
            for (int i = 0; i < nutrientsArray.length(); i++) {
                final JSONObject nutrientJson = nutrientsArray.getJSONObject(i);

                final String name = nutrientJson.optString("name", "").toLowerCase();
                final Double amount = nutrientJson.has("amount")
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
