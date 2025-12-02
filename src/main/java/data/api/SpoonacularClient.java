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

    public static final String AMOUNT = "amount";
    public static final String ID = "id";

    private static final String BASE = "https://api.spoonacular.com";
    public static final String RECIPES = "/recipes/";

    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(15))
            .build();
    private final String apiKey;

    public SpoonacularClient(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey required");
    }

    // Create the DTO

    /**
     * Retrieve full recipe information from the Spoonacular API and populates
     * a RecipeInformationDto with all available fields.
     *
     * @param id the Spoonacular recipe ID to look up
     * @param includeNutrition to load nutrition data, {@code false} skip it
     * @return a fully populated RecipeInformationDto containing all
     *         available information for the recipe
     * @throws ApiException if the API request fails, the HTTP response indicates
     *                      and error, or if any I/O or parsing error occurs during
     *                      the retrieval of recipe data
     */
    public RecipeInformationDto getRecipeInformation(long id, boolean includeNutrition) throws ApiException {

        final RecipeInformationDto dto = new RecipeInformationDto();

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
        final String url = String.format(
                "%s/recipes/findByIngredients?ingredients=%s&number=%d&offset=%d&ranking=2&apiKey=%s",
                BASE, ingredients, number, offset, apiKey
        );

        final JSONArray jsonArray = getJsonArray(url);

        final ArrayList<RecipePreview> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            final RecipePreview recipePreview = new RecipePreview();
            recipePreview.id = jsonObject.getLong(ID);
            recipePreview.recipeKey = "a" + jsonObject.getLong(ID);
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
        final String url = BASE + RECIPES + id + "/information?apiKey=" + apiKey;

        final JSONObject root = getJson(url);

        dto.setId(root.optLong(ID));
        dto.setTitle(root.optString("title", ""));
        dto.setImage(root.optString("image", ""));

        Integer servings = null;
        if (root.has("servings")) {
            servings = root.optInt("servings");
        }
        dto.setServings(servings);

        Integer readyInMinutes = null;
        if (root.has("readyInMinutes")) {
            readyInMinutes = root.optInt("readyInMinutes");
        }
        dto.setReadyInMinutes(readyInMinutes);

        dto.setSourceName(root.optString("sourceName", ""));
        dto.setSourceUrl(root.optString("sourceUrl", ""));

        // ingredients required
        final JSONArray ings = root.optJSONArray("extendedIngredients");
        if (ings != null) {
            for (int i = 0; i < ings.length(); i++) {
                final JSONObject ingredientJson = ings.getJSONObject(i);

                final String name = ingredientJson.optString("name", "");

                Double amount = null;
                if (ingredientJson.has(AMOUNT)) {
                    amount = ingredientJson.optDouble(AMOUNT);
                }

                final String unit = ingredientJson.optString("unit", "");
                final String original = ingredientJson.optString("original", "");

                // helper method on DTO, no direct ExtendedIngredient usage
                dto.addIngredient(name, amount, unit, original);
            }
        }
    }

    private void fillInstruction(long id, RecipeInformationDto dto) throws ApiException {
        final String url = BASE + RECIPES + id + "/analyzedInstructions?apiKey=" + apiKey;
        final JSONArray blocks = getJsonArray(url);
        if (!blocks.isEmpty()) {
            final JSONObject instructionBlock = blocks.getJSONObject(0);
            final JSONArray stepsArray = instructionBlock.optJSONArray("steps");
            if (stepsArray != null) {
                for (int i = 0; i < stepsArray.length(); i++) {
                    final JSONObject stepJson = stepsArray.getJSONObject(i);

                    int number = i + 1;
                    if (stepJson.has("number")) {
                        number = stepJson.optInt("number");
                    }

                    final String text = stepJson.optString("step", "");

                    // use helper method (Step class is no longer referenced here)
                    dto.addStep(number, text);
                }
            }
        }

    }

    private void fillNutrition(long id, RecipeInformationDto dto) throws ApiException {
        final String url = BASE + RECIPES + id + "/nutritionWidget.json?apiKey=" + apiKey;
        final JSONObject root = getJson(url);

        final JSONArray nutrientsArray = root.optJSONArray("nutrients");
        if (nutrientsArray != null) {
            for (int i = 0; i < nutrientsArray.length(); i++) {
                final JSONObject nutrientJson = nutrientsArray.getJSONObject(i);

                final String name = nutrientJson.optString("name", "").toLowerCase();

                Double amount = null;
                if (nutrientJson.has(AMOUNT)) {
                    amount = nutrientJson.optDouble(AMOUNT);
                }

                final String unit = nutrientJson.optString("unit", "");

                switch (name) {
                    case "calories" -> dto.setCalories(amount);
                    case "protein" -> {
                        dto.setProteinAmount(amount);
                        dto.setProteinUnit(unit);
                    }
                    case "fat" -> {
                        dto.setFatAmount(amount);
                        dto.setFatUnit(unit);
                    }
                    case "carbohydrates" -> {
                        dto.setCarbsAmount(amount);
                        dto.setCarbsUnit(unit);
                    }
                }
            }
        }
    }

    private JSONObject getJson(String url) throws ApiException {
        final String body = fetchJsonString(url);
        return new JSONObject(body);
    }

    private JSONArray getJsonArray(String url) throws ApiException {
        final String body = fetchJsonString(url);
        return new JSONArray(body);
    }

    private String fetchJsonString(String url) throws ApiException {
        final Request request = new Request.Builder().url(url).get().build();

        try (Response response = http.newCall(request).execute()) {

            final int code = response.code();
            String body = "";
            if (response.body() != null) {
                body = response.body().string();
            }

            if (code < 200 || code >= 300) {
                final String msg = mapHttpErrorMessage(code);
                throw new ApiException(msg, code, body);
            }

            return body;

        }
        catch (IOException ex) {
            throw new ApiException("Network/IO error calling " + url, ex);
        }
    }

    private String mapHttpErrorMessage(int code) {

        return switch (code) {
            case 401 -> "Unauthorized (check API key).";
            case 402, 429 -> "Quota / rate limit exceeded.";
            case 404 -> "Recipe not found.";
            default -> "HTTP error " + code;
        };
    }

    // error wrapper
    public static class ApiException extends Exception {
        private final int statusCode;
        private final String body;

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

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }
    }

}
