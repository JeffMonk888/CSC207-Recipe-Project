package demo;

import data.api.SpoonacularClient;
import data.mapper.RecipeMapper;
import data.dto.RecipeInformationDTO;
import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Recipe;
import usecase.common.MotionForRecipe;
import usecase.save_recipe.*;
import usecase.retrieve_saved.*;
import usecase.delete_saved.*;

import java.util.List;

public class CombinedDemo {
    
    private static final String API_KEY = "7379cb18b81945e4994504e9414ff7f1";

    // Real Spoonacular recipe ID to test with (numeric API id)
    private static final long REAL_API_ID_TO_TEST = 716429L;

    // What we actually store in user_recipe_links.csv for an API recipe
    private static final String REAL_RECIPE_KEY_TO_TEST = "a" + REAL_API_ID_TO_TEST;

    private static final Long TEST_USER_ID = 1L;

    private static final String RECIPE_LINKS_CSV_PATH = "user_recipe_links.csv";
    private static final String RECIPE_CACHE_JSON_PATH = "recipe_cache.json";

    public static void main(String[] args) {

        // Gateways / DAOs
        SpoonacularClient apiClient = new SpoonacularClient(API_KEY);
        MotionForRecipe userSavedRecipeDAO = new UserSavedRecipeAccessObject(RECIPE_LINKS_CSV_PATH);
        RecipeDataAssessObject recipeDAO = new RecipeDataAssessObject(RECIPE_CACHE_JSON_PATH);

        // Presenters (inline for demo)
        SaveRecipeOutputBoundary savePresenter = new SaveRecipeOutputBoundary() {
            @Override
            public void presentSuccess(SaveRecipeOutputData outputData) {
                System.out.println("Save success: " +
                        outputData.getSavedRecipe().getRecipeKey());
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Save failed: " + errorMessage);
            }
        };

        RetrieveSavedOutputBoundary retrievePresenter = outputData -> {
            List<Recipe> recipes = outputData.getSavedRecipes();
            System.out.println("Retrieve success: " + recipes.size() + " saved recipes:");
            for (Recipe recipe : recipes) {
                System.out.println(" - " + recipe.getTitle() +
                        " (id: " + recipe.getId() + ")");
            }
        };

        DeleteSavedOutputBoundary deletePresenter = new DeleteSavedOutputBoundary() {
            @Override
            public void presentSuccess(DeleteSavedOutputData outputData) {
                System.out.println("Delete success: " +
                        outputData.getDeletedRecipeKey());
            }

            @Override
            public void presentFailure(String errorMessage) {
                System.out.println("Delete failed: " + errorMessage);
            }
        };

        // Use cases
        SaveRecipeInputBoundary saveInteractor =
                new SaveRecipeInteractor(userSavedRecipeDAO, savePresenter);

        // IMPORTANT: this assumes you updated RetrieveSavedInteractor to also take apiClient
        RetrieveSavedInputBoundary retrieveInteractor =
                new RetrieveSavedInteractor(userSavedRecipeDAO, recipeDAO, apiClient, retrievePresenter);

        DeleteSavedInputBoundary deleteInteractor =
                new DeleteSavedInteractor(userSavedRecipeDAO, deletePresenter);

        try {
            // (Optional) sanity check: directly hit API once
            RecipeInformationDTO fromApi = apiClient.getRecipeInformation(REAL_API_ID_TO_TEST, true);
            Recipe recipe = RecipeMapper.toDomain(fromApi);
            System.out.println("Fetched from API (sanity check): " + recipe.getTitle());

            // 1) Save the API recipe for the user (only the key "a<id>" goes into CSV)
            System.out.println("\n--- Saving recipe ---");
            saveInteractor.execute(new SaveRecipeInputData(TEST_USER_ID, REAL_RECIPE_KEY_TO_TEST));

            // 2) Retrieve all saved recipes for this user
            //    RetrieveSavedInteractor should:
            //      - see key starts with "a"
            //      - call apiClient.getRecipeInformationDomain(apiId)
            System.out.println("\n--- Retrieving saved recipes ---");
            retrieveInteractor.execute(new RetrieveSavedInputData(TEST_USER_ID));

            // 3) Delete the saved recipe
            System.out.println("\n--- Deleting saved recipe ---");
            deleteInteractor.execute(new DeleteSavedInputData(TEST_USER_ID, REAL_RECIPE_KEY_TO_TEST));

            // 4) Retrieve again to confirm it's gone
            System.out.println("\n--- Retrieving saved recipes after delete ---");
            retrieveInteractor.execute(new RetrieveSavedInputData(TEST_USER_ID));

        } catch (SpoonacularClient.ApiException e) {
            System.out.println("API error while getting data from Spoonacular: " + e.getMessage());
        }
    }
}
