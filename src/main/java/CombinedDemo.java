import data.api.SpoonacularClient;
import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;
import domain.entity.Recipe;
import usecase.common.MotionForRecipe;
import data.mapper.RecipeMapper;
import usecase.save_recipe.*;
import usecase.retrieve_saved.*;
import usecase.delete_saved.*;

import java.util.List;


public class CombinedDemo {


    private static final String API_KEY = "7379cb18b81945e4994504e9414ff7f1";

    private static final long REAL_RECIPE_ID_TO_TEST = 716429L;
    private static final Long TEST_USER_ID = 1L;

    private static final String RECIPE_LINKS_CSV_PATH = "user_recipe_links.csv";
    private static final String RECIPE_CACHE_JSON_PATH = "recipe_cache.json";


    public static void main(String[] args) {

        SpoonacularClient apiClient = new SpoonacularClient(API_KEY);

        MotionForRecipe userSavedRecipeDAO = new UserSavedRecipeAccessObject(RECIPE_LINKS_CSV_PATH);

        RecipeDataAssessObject recipeDAO = new RecipeDataAssessObject(RECIPE_CACHE_JSON_PATH);

        SaveRecipeOutputBoundary savePresenter = new SaveRecipeOutputBoundary() {
            public void presentSuccess(SaveRecipeOutputData outputData) {
                System.out.println("success: " + outputData.getSavedRecipe().getRecipeId());
            }
            public void presentFailure(String errorMessage) {
                System.out.println("filled: " + errorMessage);
            }
        };
        RetrieveSavedOutputBoundary retrievePresenter = (outputData) -> {
            List<Recipe> recipes = outputData.getSavedRecipes();
            System.out.println("success: " + recipes.size() + " savedrecipes:");
            for (Recipe recipe : recipes) {
                System.out.println(" - " + recipe.getTitle() + " (ID: " + recipe.getId() + ")");
            }
        };
        DeleteSavedOutputBoundary deletePresenter = new DeleteSavedOutputBoundary() {
            public void presentSuccess(DeleteSavedOutputData outputData) {
                System.out.println("success " + outputData.getDeletedRecipeId());
            }
            public void presentFailure(String errorMessage) {
                System.out.println("filled: " + errorMessage);
            }
        };

        SaveRecipeInputBoundary saveInteractor = new SaveRecipeInteractor(userSavedRecipeDAO, savePresenter);
        RetrieveSavedInputBoundary retrieveInteractor = new RetrieveSavedInteractor(userSavedRecipeDAO, recipeDAO, retrievePresenter);
        DeleteSavedInputBoundary deleteInteractor = new DeleteSavedInteractor(userSavedRecipeDAO, deletePresenter);


        // set up
        try {
            if (!recipeDAO.exists(REAL_RECIPE_ID_TO_TEST)) {
                data.dto.RecipeInformationDTO dto = apiClient.getRecipeInformation(REAL_RECIPE_ID_TO_TEST, true);
                Recipe realRecipe = RecipeMapper.toDomain(dto);
                recipeDAO.save(realRecipe);
            }

            // save
            saveInteractor.execute(new SaveRecipeInputData(TEST_USER_ID, REAL_RECIPE_ID_TO_TEST));

            // retrieve
            retrieveInteractor.execute(new RetrieveSavedInputData(TEST_USER_ID));

            // delete
            deleteInteractor.execute(new DeleteSavedInputData(TEST_USER_ID, REAL_RECIPE_ID_TO_TEST));

            // retrieve again
            retrieveInteractor.execute(new RetrieveSavedInputData(TEST_USER_ID));

        } catch (data.api.SpoonacularClient.ApiException e) {
            System.out.println("error about getting data from api " + e.getMessage());
        }

    }
}
