package app;

import interface_adapter.ViewManagerModel;
import view.FridgeView;
import view.HomeView;
import view.LoginView;
import view.SignUpView;
import view.ViewManager;
import view.ViewRecipeView;

public class AppBuilder {
    private final ViewManagerModel viewManagerModel;
    private final ViewManager viewManager;

    private LoginView loginView;
    private SignUpView signUpView;
    private HomeView homeView;
    private FridgeView fridgeView;
    private ViewRecipeView viewRecipeView;

    public AppBuilder() {
        viewManagerModel = new ViewManagerModel();
        viewManager = new ViewManager(viewManagerModel);
    }

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
    
    public void show() {
        viewManagerModel.setActiveViewName(loginView.getViewName());
        viewManager.show();
    }
}
