package app;

// API + saved-recipe data access
import data.api.SpoonacularClient;
import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;

// View manager & common
import interface_adapter.ViewManagerModel;

// Create Recipe
import interface_adapter.create_recipe.CreateRecipeController;
import interface_adapter.create_recipe.CreateRecipePresenter;
import interface_adapter.create_recipe.CreateRecipeViewModel;

// Fridge
import interface_adapter.fridge.FridgeController;
import interface_adapter.fridge.FridgePresenter;
import interface_adapter.fridge.FridgeViewModel;

// Saved Recipes + View Recipe adapters
import interface_adapter.saved_recipe.SavedRecipeController;
import interface_adapter.saved_recipe.SavedRecipePresenter;
import interface_adapter.saved_recipe.SavedRecipeViewModel;

import interface_adapter.view_recipe.ViewRecipeController;
import interface_adapter.view_recipe.ViewRecipePresenter;
import interface_adapter.view_recipe.ViewRecipeViewModel;
import interface_adapter.view_recipe.ViewRecipeFromSavedPresenter;

// NEW: Save Recipe adapters
import interface_adapter.saved_recipe.SaveRecipeController;
import interface_adapter.saved_recipe.SaveRecipePresenter;

// NEW: Save Recipe use case
import usecase.save_recipe.SaveRecipeInputBoundary;
import usecase.save_recipe.SaveRecipeInteractor;
import usecase.save_recipe.SaveRecipeOutputBoundary;

// Search-by-fridge feature
import interface_adapter.search_by_fridge.SearchByFridgeController;
import interface_adapter.search_by_fridge.SearchByFridgePresenter;
import interface_adapter.search_by_fridge.SearchByFridgeViewModel;

// Rate Recipe feature
import interface_adapter.rate_recipe.RateRecipeController;
import interface_adapter.rate_recipe.RateRecipePresenter;
import interface_adapter.rate_recipe.RateRecipeViewModel;

// Use cases: Search-by-fridge & Rate recipe
import usecase.search_by_fridge.SearchByFridgeInputBoundary;
import usecase.search_by_fridge.SearchByFridgeInteractor;
import usecase.search_by_fridge.SearchByFridgeOutputBoundary;

import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;

import usecase.common.RecipeByIngredientsAccess;

// Core use cases
import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInteractor;
import usecase.common.FridgeAccess;
import usecase.create_recipe.CreateRecipeInputBoundary;
import usecase.create_recipe.CreateRecipeInteractor;
import usecase.create_recipe.CreateRecipeOutputBoundary;
import usecase.delete_saved.DeleteSavedInputBoundary;
import usecase.delete_saved.DeleteSavedInteractor;
import usecase.retrieve_saved.RetrieveSavedInputBoundary;
import usecase.retrieve_saved.RetrieveSavedInteractor;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInteractor;
import usecase.view_recipe.ViewRecipeOutputBoundary;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInteractor;

// Views
import view.CreateRecipeView;
import view.FindRecipeView;
import view.FridgeView;
import view.HomeView;
import view.LoginView;
import view.SavedRecipesView;
import view.SearchByFridgeView;
import view.SignUpView;
import view.ViewManager;
import view.ViewRecipeNoSave;
import view.ViewRecipeView;

public class AppBuilder {

    // Fallback API key if env var is not set (for local demo)
    String masterApiKey = "ecb55412e8db47218210d2b35c07fc1a";

    private final ViewManagerModel viewManagerModel;
    private final ViewManager viewManager;
    private final FridgeAccess fridgeAccess;
    private final UserSavedRecipeAccessObject userSavedRecipeDAO;
    private final RecipeDataAssessObject recipeDAO;

    private LoginView loginView;
    private SignUpView signUpView;
    private HomeView homeView;
    private FridgeView fridgeView;
    private CreateRecipeView createRecipeView;
    private SavedRecipesView savedRecipesView;
    private FindRecipeView findRecipeView;
    private SearchByFridgeView searchByFridgeView;

    // Shared controllers for “view recipe” flows
    private ViewRecipeController viewRecipeController;          // for API / search / fridge flows
    private ViewRecipeController viewRecipeFromSavedController; // for SavedRecipesView only

    public AppBuilder(FridgeAccess fridgeAccess) {
        this.viewManagerModel = new ViewManagerModel();
        this.viewManager = new ViewManager(viewManagerModel);
        this.fridgeAccess = fridgeAccess;

        // Shared DAOs for the whole app
        this.userSavedRecipeDAO = new UserSavedRecipeAccessObject("user_recipe_links.csv");
        this.recipeDAO = new RecipeDataAssessObject("recipe_cache.json");
    }

    // ============ Basic top-level views ============

    public AppBuilder addLoginView() {
        loginView = new LoginView(viewManagerModel);
        viewManager.addView(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addSignUpView() {
        signUpView = new SignUpView(viewManagerModel);
        viewManager.addView(signUpView, signUpView.getViewName());
        return this;
    }

    public AppBuilder addHomeView() {
        homeView = new HomeView(viewManagerModel);
        viewManager.addView(homeView, homeView.getViewName());
        return this;
    }

    // ============ Fridge feature ============

    public AppBuilder addFridgeFeature() {

        FridgeViewModel fridgeViewModel = new FridgeViewModel();
        FridgePresenter fridgePresenter = new FridgePresenter(fridgeViewModel);

        AddIngredientInputBoundary addInteractor =
                new AddIngredientInteractor(fridgeAccess, fridgePresenter);

        RemoveIngredientInputBoundary removeInteractor =
                new RemoveIngredientInteractor(fridgeAccess, fridgePresenter);

        FridgeController fridgeController =
                new FridgeController(addInteractor, removeInteractor);

        fridgeView = new FridgeView(fridgeController, fridgeViewModel, viewManagerModel);
        viewManager.addView(fridgeView, fridgeView.getViewName());

        return this;
    }

    // ============ Create Recipe feature ============

    public AppBuilder addCreateRecipeFeature() {

        // View model & presenter
        CreateRecipeViewModel createRecipeViewModel = new CreateRecipeViewModel();
        CreateRecipeOutputBoundary presenter =
                new CreateRecipePresenter(createRecipeViewModel, viewManagerModel);

        // Interactor & controller
        CreateRecipeInputBoundary interactor =
                new CreateRecipeInteractor(this.recipeDAO, this.userSavedRecipeDAO, presenter);
        CreateRecipeController controller =
                new CreateRecipeController(interactor);

        // Swing view
        createRecipeView = new CreateRecipeView(
                createRecipeViewModel,
                controller,
                viewManagerModel
        );

        viewManager.addView(createRecipeView, createRecipeView.getViewName());
        return this;
    }

    // ============ Saved Recipes + View Recipe + Rate Recipe ============

    public AppBuilder addSavedRecipesFeature() {

        // 1) API + DAOs
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = masterApiKey;
        }
        SpoonacularClient apiClient = new SpoonacularClient(apiKey);

        // 2) SAVE RECIPE USE CASE (used by ViewRecipeView)
        SaveRecipeOutputBoundary saveRecipePresenter =
                new SaveRecipePresenter();
        SaveRecipeInputBoundary saveRecipeInteractor =
                new SaveRecipeInteractor(this.userSavedRecipeDAO, saveRecipePresenter);
        SaveRecipeController saveRecipeController =
                new SaveRecipeController(saveRecipeInteractor);

        // 3) SHARED ViewRecipeViewModel (one VM / two presenters)
        ViewRecipeViewModel viewRecipeViewModel = new ViewRecipeViewModel();

        // 4) NORMAL VIEW RECIPE (WITH SAVE) – used by search/API flows
        ViewRecipeOutputBoundary viewRecipePresenter =
                new ViewRecipePresenter(viewRecipeViewModel, viewManagerModel);

        ViewRecipeInputBoundary viewRecipeInteractor =
                new ViewRecipeInteractor(apiClient, this.recipeDAO, viewRecipePresenter);

        viewRecipeController = new ViewRecipeController(viewRecipeInteractor);

        ViewRecipeView viewRecipeView =
                new ViewRecipeView(viewRecipeViewModel, saveRecipeController, viewManagerModel);

        viewManager.addView(viewRecipeView, viewRecipeViewModel.getViewName());

        // 5) NO-SAVE VIEW RECIPE – used ONLY from SavedRecipesView
        ViewRecipeOutputBoundary fromSavedPresenter =
                new ViewRecipeFromSavedPresenter(viewRecipeViewModel, viewManagerModel);

        ViewRecipeInputBoundary fromSavedInteractor =
                new ViewRecipeInteractor(apiClient, this.recipeDAO, fromSavedPresenter);

        viewRecipeFromSavedController = new ViewRecipeController(fromSavedInteractor);

        ViewRecipeNoSave viewRecipeNoSave =
                new ViewRecipeNoSave(viewRecipeViewModel, null, viewManagerModel);

        viewManager.addView(viewRecipeNoSave, ViewRecipeNoSave.VIEW_NAME);

        // 6) RATE RECIPE USE CASE – SavedRecipesView -> RateRecipeView
        RateRecipeViewModel rateRecipeViewModel = new RateRecipeViewModel();
        RateRecipeOutputBoundary rateRecipePresenter =
                new RateRecipePresenter(rateRecipeViewModel, viewManagerModel);
        RateRecipeInputBoundary rateRecipeInteractor =
                new RateRecipeInteractor(this.userSavedRecipeDAO, rateRecipePresenter);
        RateRecipeController rateRecipeController =
                new RateRecipeController(rateRecipeInteractor);

        // 7) SAVED RECIPES LIST
        SavedRecipeViewModel savedRecipeViewModel = new SavedRecipeViewModel();
        SavedRecipePresenter savedPresenter =
                new SavedRecipePresenter(savedRecipeViewModel, viewManagerModel);

        RetrieveSavedInputBoundary retrieveInteractor =
                new RetrieveSavedInteractor(
                        this.userSavedRecipeDAO,
                        this.recipeDAO,
                        apiClient,
                        savedPresenter
                );
        DeleteSavedInputBoundary deleteInteractor =
                new DeleteSavedInteractor(this.userSavedRecipeDAO, savedPresenter);

        SavedRecipeController savedController =
                new SavedRecipeController(retrieveInteractor, deleteInteractor);

        // SAVED RECIPES VIEW:
        //  - uses viewRecipeFromSavedController to open recipes without duplicate saves
        //  - uses rateRecipeController + rateRecipeViewModel to open RateRecipeView
        //  - passes userSavedRecipeDAO so CategoryView / RateRecipeView can use the real gateway
        savedRecipesView = new SavedRecipesView(
                savedController,
                savedRecipeViewModel,
                viewRecipeFromSavedController,
                viewManagerModel,
                rateRecipeController,
                rateRecipeViewModel,
                this.userSavedRecipeDAO
        );

        viewManager.addView(savedRecipesView, savedRecipesView.getViewName());
        return this;
    }

    // ============ Search by Fridge feature ============

    public AppBuilder addSearchByFridgeFeature() {
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = masterApiKey;
        }
        SpoonacularClient spoonacularClient = new SpoonacularClient(apiKey);
        RecipeByIngredientsAccess recipeAccess = spoonacularClient;

        SearchByFridgeViewModel vm = new SearchByFridgeViewModel();
        SearchByFridgeOutputBoundary presenter = new SearchByFridgePresenter(vm);

        SearchByFridgeInputBoundary interactor =
                new SearchByFridgeInteractor(fridgeAccess, recipeAccess, presenter);
        SearchByFridgeController controller =
                new SearchByFridgeController(interactor);

        // When a recipe is selected from search, open the "normal" ViewRecipeView
        SearchByFridgeView.RecipeSelectionListener listener = recipeKey -> {
            if (viewRecipeController != null) {
                viewRecipeController.execute(recipeKey);
            } else {
                System.err.println("ViewRecipeController is null. Did you call addSavedRecipesFeature()?");
            }
        };
        searchByFridgeView =
                new SearchByFridgeView(controller, vm, viewManagerModel, listener);

        viewManager.addView(searchByFridgeView, searchByFridgeView.getViewName());
        return this;
    }

    // ============ Simple Find Recipe (if you still use this) ============

    public AppBuilder addFindRecipe() {
        findRecipeView = new FindRecipeView(viewManagerModel);
        viewManager.addView(findRecipeView, findRecipeView.getViewName());
        return this;
    }

    // ============ Show app ============

    public void show() {
        viewManagerModel.setActiveViewName(loginView.getViewName());
        viewManager.show();
    }
}
