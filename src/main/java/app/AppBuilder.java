package app;
import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;

import interface_adapter.ViewManagerModel;

// fridge
import interface_adapter.fridge.FridgeController;
import interface_adapter.fridge.FridgePresenter;
import interface_adapter.fridge.FridgeViewModel;

// create recipe
import interface_adapter.create_recipe.CreateRecipeController;
import interface_adapter.create_recipe.CreateRecipePresenter;
import interface_adapter.create_recipe.CreateRecipeViewModel;

import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInteractor;
import usecase.common.FridgeAccess;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInteractor;

import usecase.create_recipe.CreateRecipeInputBoundary;
import usecase.create_recipe.CreateRecipeInteractor;
import usecase.create_recipe.CreateRecipeOutputBoundary;

import view.CreateRecipeView;
import view.FridgeView;
import view.HomeView;
import view.LoginView;
import view.SignUpView;
import view.ViewManager;
import view.ViewRecipeView;

public class AppBuilder {
    private final ViewManagerModel viewManagerModel;
    private final ViewManager viewManager;
    private final FridgeAccess fridgeAccess;

    private LoginView loginView;
    private SignUpView signUpView;
    private HomeView homeView;
    private FridgeView fridgeView;
    private CreateRecipeView createRecipeView;

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

    public AppBuilder(FridgeAccess fridgeAccess) {
        this.viewManagerModel = new ViewManagerModel();
        this.viewManager = new ViewManager(viewManagerModel);
        this.fridgeAccess = fridgeAccess;
    }

    public AppBuilder addFridgeFeature(Long userId) {

        FridgeViewModel fridgeViewModel = new FridgeViewModel();
        FridgePresenter fridgePresenter = new FridgePresenter(fridgeViewModel);

        AddIngredientInputBoundary addInteractor =
                new AddIngredientInteractor(fridgeAccess, fridgePresenter);

        RemoveIngredientInputBoundary removeInteractor =
                new RemoveIngredientInteractor(fridgeAccess, fridgePresenter);

        FridgeController fridgeController =
                new FridgeController(addInteractor, removeInteractor);

        fridgeView = new FridgeView(fridgeController, fridgeViewModel, userId);
        viewManager.addView(fridgeView, fridgeView.getViewName());

        return this;
    }

    public AppBuilder addCreateRecipeFeature(Long userId) {
        // Data access, same as in CreateRecipeDemo
        UserSavedRecipeAccessObject userSavedRecipeDAO =
                new UserSavedRecipeAccessObject("user_recipe_links.csv");
        RecipeDataAssessObject recipeDAO =
                new RecipeDataAssessObject("recipe_cache.json");

        // View model & presenter
        CreateRecipeViewModel createRecipeViewModel = new CreateRecipeViewModel();
        CreateRecipeOutputBoundary presenter =
                new CreateRecipePresenter(createRecipeViewModel, viewManagerModel);

        // Interactor & controller
        CreateRecipeInputBoundary interactor =
                new CreateRecipeInteractor(recipeDAO, userSavedRecipeDAO, presenter);
        CreateRecipeController controller =
                new CreateRecipeController(interactor);

        // Swing view
        createRecipeView = new CreateRecipeView(
                createRecipeViewModel,
                controller,
                viewManagerModel,
                userId
        );

        viewManager.addView(createRecipeView, createRecipeView.getViewName());
        return this;
    }

    public void show() {
        viewManagerModel.setActiveViewName(loginView.getViewName());
        viewManager.show();
    }
}
