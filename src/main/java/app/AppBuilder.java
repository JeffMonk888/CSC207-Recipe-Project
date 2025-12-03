package app;

// API + saved-recipe data access
import data.api.SpoonacularClient;
import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.create_recipe.CreateRecipeAbstractViewModel;
import interfaceadapter.create_recipe.CreateRecipeController;
import interfaceadapter.create_recipe.CreateRecipePresenter;
import interfaceadapter.fridge.FridgeAbstractViewModel;
import interfaceadapter.fridge.FridgeController;
import interfaceadapter.fridge.FridgePresenter;
import interfaceadapter.rate_recipe.RateRecipeAbstractViewModel;
import interfaceadapter.rate_recipe.RateRecipeController;
import interfaceadapter.rate_recipe.RateRecipePresenter;
import interfaceadapter.saved_recipe.SaveRecipeController;
import interfaceadapter.saved_recipe.SaveRecipePresenter;
import interfaceadapter.saved_recipe.SavedRecipeAbstractViewModel;
import interfaceadapter.saved_recipe.SavedRecipeController;
import interfaceadapter.saved_recipe.SavedRecipePresenter;
import interfaceadapter.search_by_fridge.SearchByFridgeAbstractViewModel;
import interfaceadapter.search_by_fridge.SearchByFridgeController;
import interfaceadapter.search_by_fridge.SearchByFridgePresenter;
import interfaceadapter.view_recipe.ViewRecipeAbstractViewModel;
import interfaceadapter.view_recipe.ViewRecipeController;
import interfaceadapter.view_recipe.ViewRecipeFromSavedPresenter;
import interfaceadapter.view_recipe.ViewRecipePresenter;
import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInteractor;
import usecase.common.FridgeAccess;
import usecase.common.RecipeByIngredientsAccess;
import usecase.create_recipe.CreateRecipeInputBoundary;
import usecase.create_recipe.CreateRecipeInteractor;
import usecase.create_recipe.CreateRecipeOutputBoundary;
import usecase.delete_saved.DeleteSavedInputBoundary;
import usecase.delete_saved.DeleteSavedInteractor;
import usecase.rate_recipe.RateRecipeInputBoundary;
import usecase.rate_recipe.RateRecipeInteractor;
import usecase.rate_recipe.RateRecipeOutputBoundary;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInteractor;
import usecase.retrieve_saved.RetrieveSavedInputBoundary;
import usecase.retrieve_saved.RetrieveSavedInteractor;
import usecase.save_recipe.SaveRecipeInputBoundary;
import usecase.save_recipe.SaveRecipeInteractor;
import usecase.save_recipe.SaveRecipeOutputBoundary;
import usecase.search_by_fridge.SearchByFridgeInputBoundary;
import usecase.search_by_fridge.SearchByFridgeInteractor;
import usecase.search_by_fridge.SearchByFridgeOutputBoundary;
import usecase.view_recipe.ViewRecipeInputBoundary;
import usecase.view_recipe.ViewRecipeInteractor;
import usecase.view_recipe.ViewRecipeOutputBoundary;
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

// Choosing to ignore checkstyle warning here, more clean as is.
public class AppBuilder {
    private final ViewManagerModel viewManagerModel;
    private final ViewManager viewManager;
    private final FridgeAccess fridgeAccess;
    private final UserSavedRecipeAccessObject userSavedRecipeDao;
    private final RecipeDataAssessObject recipeDao;

    private LoginView loginView;
    private ViewRecipeController viewRecipeController;

    public AppBuilder(FridgeAccess fridgeAccess) {
        this.viewManagerModel = new ViewManagerModel();
        this.viewManager = new ViewManager(viewManagerModel);
        this.fridgeAccess = fridgeAccess;
        this.userSavedRecipeDao = new UserSavedRecipeAccessObject("user_recipe_links.csv");
        this.recipeDao = new RecipeDataAssessObject("recipe_cache.json");
    }
    /**
     * Creates the {@link LoginView}, registers it with the view manager,
     * and makes it available to the application.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addLoginView() {
        loginView = new LoginView(viewManagerModel);
        viewManager.addView(loginView, loginView.getViewName());
        return this;
    }
    /**
     * Creates the {@link SignUpView}, registers it with the view manager,
     * and makes it available to the application.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addSignUpView() {
        final SignUpView signUpView = new SignUpView(viewManagerModel);
        viewManager.addView(signUpView, signUpView.getViewName());
        return this;
    }
    /**
     * Creates the {@link HomeView}, registers it with the view manager,
     * and makes it available as the main post-login screen.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addHomeView() {
        final HomeView homeView = new HomeView(viewManagerModel);
        viewManager.addView(homeView, homeView.getViewName());
        return this;
    }

    /**
     * Registers the fridge feature, wiring its view, presenter, interactor,
     * and data access so users can manage items in their fridge.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addFridgeFeature() {

        final FridgeAbstractViewModel fridgeViewModel = new FridgeAbstractViewModel();
        final FridgePresenter fridgePresenter = new FridgePresenter(fridgeViewModel);

        final AddIngredientInputBoundary addInteractor =
                new AddIngredientInteractor(fridgeAccess, fridgePresenter);

        final RemoveIngredientInputBoundary removeInteractor =
                new RemoveIngredientInteractor(fridgeAccess, fridgePresenter);

        final FridgeController fridgeController =
                new FridgeController(addInteractor, removeInteractor);

        final FridgeView fridgeView = new FridgeView(fridgeController, fridgeViewModel, viewManagerModel);
        viewManager.addView(fridgeView, fridgeView.getViewName());

        return this;
    }
    /**
     * Registers the feature that allows users to create and submit
     * new recipes in the application.
     *
     * @return this builder to enable method chaining
     */

    public AppBuilder addCreateRecipeFeature() {

        // View model & presenter
        final CreateRecipeAbstractViewModel createRecipeViewModel = new CreateRecipeAbstractViewModel();
        final CreateRecipeOutputBoundary presenter =
                new CreateRecipePresenter(createRecipeViewModel, viewManagerModel);

        // Interactor & controller
        final CreateRecipeInputBoundary interactor =
                new CreateRecipeInteractor(this.recipeDao, this.userSavedRecipeDao, presenter);
        final CreateRecipeController controller =
                new CreateRecipeController(interactor);

        // Swing view
        final CreateRecipeView createRecipeView = new CreateRecipeView(
                createRecipeViewModel,
                controller,
                viewManagerModel
        );

        viewManager.addView(createRecipeView, createRecipeView.getViewName());
        return this;
    }
    /**
     * Registers the feature that allows users to view and manage
     * their saved recipes within the application.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addSavedRecipesFeature() {

        // 1) API + DAOs
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "ecb55412e8db47218210d2b35c07fc1a";
        }
        final SpoonacularClient apiClient = new SpoonacularClient(apiKey);

        // 2) SAVE RECIPE USE CASE
        final SaveRecipeOutputBoundary saveRecipePresenter =
                new SaveRecipePresenter();
        final SaveRecipeInputBoundary saveRecipeInteractor =
                new SaveRecipeInteractor(this.userSavedRecipeDao, saveRecipePresenter);
        final SaveRecipeController saveRecipeController =
                new SaveRecipeController(saveRecipeInteractor);

        // 3) SHARED ViewRecipeViewModel
        final ViewRecipeAbstractViewModel viewRecipeViewModel = new ViewRecipeAbstractViewModel();

        // 4) NORMAL VIEW RECIPE (WITH SAVE) – used by search/API flows
        final ViewRecipeOutputBoundary viewRecipePresenter =
                new ViewRecipePresenter(viewRecipeViewModel, viewManagerModel);

        final ViewRecipeInputBoundary viewRecipeInteractor =
                new ViewRecipeInteractor(apiClient, this.recipeDao, viewRecipePresenter);

        viewRecipeController = new ViewRecipeController(viewRecipeInteractor);

        final ViewRecipeView viewRecipeView =
                new ViewRecipeView(viewRecipeViewModel, saveRecipeController, viewManagerModel);

        viewManager.addView(viewRecipeView, viewRecipeViewModel.getViewName());

        // 5) NO-SAVE VIEW RECIPE – used ONLY from SavedRecipesView
        final ViewRecipeOutputBoundary fromSavedPresenter =
                new ViewRecipeFromSavedPresenter(viewRecipeViewModel, viewManagerModel);

        final ViewRecipeInputBoundary fromSavedInteractor =
                new ViewRecipeInteractor(apiClient, this.recipeDao, fromSavedPresenter);

        final ViewRecipeController viewRecipeFromSavedController = new ViewRecipeController(fromSavedInteractor);

        final ViewRecipeNoSave viewRecipeNoSave =
                new ViewRecipeNoSave(viewRecipeViewModel, null, viewManagerModel);

        viewManager.addView(viewRecipeNoSave, ViewRecipeNoSave.VIEW_NAME);

        // 6) RATE RECIPE USE CASE – for SavedRecipesView -> RateRecipeView
        final RateRecipeAbstractViewModel rateRecipeViewModel =
                new RateRecipeAbstractViewModel();
        final RateRecipeOutputBoundary rateRecipePresenter =
                new RateRecipePresenter(rateRecipeViewModel, viewManagerModel);
        final RateRecipeInputBoundary rateRecipeInteractor =
                new RateRecipeInteractor(this.userSavedRecipeDao, rateRecipePresenter);
        final RateRecipeController rateRecipeController =
                new RateRecipeController(rateRecipeInteractor);

        // 7) SAVED RECIPES LIST
        final SavedRecipeAbstractViewModel savedRecipeViewModel = new SavedRecipeAbstractViewModel();

        final SavedRecipePresenter savedPresenter =
                new SavedRecipePresenter(savedRecipeViewModel, viewManagerModel);

        final RetrieveSavedInputBoundary retrieveInteractor =
                new RetrieveSavedInteractor(this.userSavedRecipeDao, this.recipeDao, apiClient, savedPresenter);
        final DeleteSavedInputBoundary deleteInteractor =
                new DeleteSavedInteractor(this.userSavedRecipeDao, savedPresenter);

        final SavedRecipeController savedController =
                new SavedRecipeController(retrieveInteractor, deleteInteractor);

        // Inject from-saved controller + rate recipe wiring
        final SavedRecipesView savedRecipesView = new SavedRecipesView(
                savedController,
                savedRecipeViewModel,
                viewRecipeFromSavedController,
                viewManagerModel,
                rateRecipeController,
                rateRecipeViewModel,
                this.userSavedRecipeDao
        );

        viewManager.addView(savedRecipesView, savedRecipesView.getViewName());
        return this;
    }

    /**
     * Configures and registers the “search by fridge contents” feature
     * in the application.
     *
     * @return this builder to allow method chaining
     */

    public AppBuilder addSearchByFridgeFeature() {
        String apiKey = System.getenv("SPOONACULAR_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "ecb55412e8db47218210d2b35c07fc1a";
        }
        final RecipeByIngredientsAccess recipeAccess = new SpoonacularClient(apiKey);

        final SearchByFridgeAbstractViewModel vm = new SearchByFridgeAbstractViewModel();
        final SearchByFridgeOutputBoundary presenter = new SearchByFridgePresenter(vm);
        final SearchByFridgeInputBoundary interactor =
                new SearchByFridgeInteractor(fridgeAccess, recipeAccess, presenter);
        final SearchByFridgeController controller =
                new SearchByFridgeController(interactor);

        final SearchByFridgeView.RecipeSelectionListener listener = recipeKey -> {
            if (viewRecipeController != null) {
                viewRecipeController.execute(recipeKey);
            }
            else {
                System.err.println("ViewRecipeController is null. Did you call addSavedRecipesFeature()?");
            }
        };
        final SearchByFridgeView searchByFridgeView =
                new SearchByFridgeView(controller, vm, viewManagerModel, listener);

        viewManager.addView(searchByFridgeView, searchByFridgeView.getViewName());
        return this;
    }
    /**
     * Creates and registers the {@link FindRecipeView} with the view manager.
     *
     * @return this builder so that calls can be chained
     */

    public AppBuilder addFindRecipe() {
        final FindRecipeView findRecipeView = new FindRecipeView(viewManagerModel);
        viewManager.addView(findRecipeView, findRecipeView.getViewName());
        return this;
    }
    /**
     * Shows the application window, starting on the login view.
     */

    public void show() {
        viewManagerModel.setActiveViewName(loginView.getViewName());
        viewManager.show();
    }
}
