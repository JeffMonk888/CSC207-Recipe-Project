package data.api;

import data.dto.RecipeInformationDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//for UC5: View domain.entity.Recipe Details
public class SpoonacularClient {

    private static final String BASE = "https://api.spoonacular.com";
    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(15))
            .build();
    private final String apiKey;

    public SpoonacularClient(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey required");
    }

    /** GET /recipes/{id}/information?includeNutrition=... */
    public RecipeInformationDTO getRecipeInformation(long id, boolean includeNutrition) throws ApiException {
        String url = String.format("%s/recipes/%d/information?includeNutrition=%s&apiKey=%s",
                BASE, id, includeNutrition, apiKey);

        Request req = new Request.Builder().url(url).get().build();
        try (Response resp = http.newCall(req).execute()) {
            int code = resp.code();
            String body = resp.body() != null ? resp.body().string() : "";
            if (code < 200 || code >= 300) {
                String msg = switch (code) {
                    case 401 -> "Unauthorized (check API key).";
                    case 402, 429 -> "Quota / rate limit exceeded.";
                    case 404 -> "domain.entity.Recipe not found.";
                    default -> "HTTP error " + code;
                };
                throw new ApiException(msg, code, body);
            }
            return parseRecipeInformation(new JSONObject(body));
        } catch (IOException e) {
            throw new ApiException("Network/IO error: " + e.getMessage(), e);
        }
    }

    // -------- parsing (kept local to the data layer) --------
    private static RecipeInformationDTO parseRecipeInformation(JSONObject o) {
        RecipeInformationDTO dto = new RecipeInformationDTO();
        dto.id = o.optLong("id");
        dto.title = o.optString("title", "");
        dto.image = o.optString("image", "");
        dto.servings = o.optInt("servings", 0);
        dto.readyInMinutes = o.optInt("readyInMinutes", 0);
        dto.sourceName = o.optString("sourceName", "");
        dto.sourceUrl = o.optString("sourceUrl", "");

        // ingredients
        JSONArray ing = o.optJSONArray("extendedIngredients");
        if (ing != null) {
            for (int i = 0; i < ing.length(); i++) {
                JSONObject ei = ing.getJSONObject(i);
                var xx = new RecipeInformationDTO.ExtendedIngredient();
                xx.name = ei.optString("name", "");
                xx.amount = ei.has("amount") ? ei.optDouble("amount") : null;
                xx.unit = ei.optString("unit", "");
                xx.original = ei.optString("original", "");
                dto.extendedIngredients.add(xx);
            }
        }

        // steps (take first instruction set if present)
        JSONArray instr = o.optJSONArray("analyzedInstructions");
        if (instr != null && !instr.isEmpty()) {
            JSONArray steps = instr.getJSONObject(0).optJSONArray("steps");
            if (steps != null) {
                for (int i = 0; i < steps.length(); i++) {
                    JSONObject s = steps.getJSONObject(i);
                    var st = new RecipeInformationDTO.Step();
                    st.number = s.optInt("number", i + 1);
                    st.step = s.optString("step", "");
                    dto.steps.add(st);
                }
            }
        }

        // nutrition
        JSONObject nutrition = o.optJSONObject("nutrition");
        if (nutrition != null) {
            JSONArray nutrients = nutrition.optJSONArray("nutrients");
            if (nutrients != null) {
                for (int i = 0; i < nutrients.length(); i++) {
                    JSONObject n = nutrients.getJSONObject(i);
                    var nu = new RecipeInformationDTO.Nutrient();
                    nu.name = n.optString("name", "");
                    nu.amount = n.has("amount") ? n.optDouble("amount") : null;
                    nu.unit = n.optString("unit", "");
                    dto.nutrients.add(nu);
                }
            }
        }
        return dto;
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
