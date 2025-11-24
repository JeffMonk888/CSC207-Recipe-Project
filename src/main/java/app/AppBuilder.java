package app;

import interface_adapter.ViewManagerModel;
import view.FridgeView;
import view.HomeView;
import view.LoginView;
import view.SignUpView;
import view.ViewManager;
import view.ViewRecipeView;
import interface_adapter.fridge.FridgeController;
import interface_adapter.fridge.FridgePresenter;
import interface_adapter.fridge.FridgeViewModel;
import usecase.add_ingredient.AddIngredientInputBoundary;
import usecase.add_ingredient.AddIngredientInteractor;
import usecase.common.FridgeAccess;
import usecase.remove_ingredient.RemoveIngredientInputBoundary;
import usecase.remove_ingredient.RemoveIngredientInteractor;
import usecase.common.FridgeAccess;

public class AppBuilder {
    private final ViewManagerModel viewManagerModel;
    private final ViewManager viewManager;
    private final FridgeAccess fridgeAccess;

    private LoginView loginView;
    private SignUpView signUpView;
    private HomeView homeView;
    private FridgeView fridgeView;

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

    public void show() {
        viewManagerModel.setActiveViewName(loginView.getViewName());
        viewManager.show();
    }
}
