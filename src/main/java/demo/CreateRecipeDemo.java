package demo;

import data.saved_recipe.RecipeDataAssessObject;
import data.saved_recipe.UserSavedRecipeAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_recipe.CreateRecipeController;
import interface_adapter.create_recipe.CreateRecipePresenter;
import interface_adapter.create_recipe.CreateRecipeViewModel;
import usecase.create_recipe.CreateRecipeInputBoundary;
import usecase.create_recipe.CreateRecipeInteractor;
import usecase.create_recipe.CreateRecipeOutputBoundary;
import view.CreateRecipeView;

import javax.swing.*;
import java.io.IOException;

public class CreateRecipeDemo {

    public static void main(String[] args) {
            UserSavedRecipeAccessObject userSavedRecipeDAO = new UserSavedRecipeAccessObject("user_recipe_links.csv");
            RecipeDataAssessObject recipeDAO = new RecipeDataAssessObject("recipe_cache.json");

            Long testUserId = 1L;
            ViewManagerModel viewManagerModel = new ViewManagerModel();
            CreateRecipeViewModel viewModel = new CreateRecipeViewModel();
            CreateRecipeOutputBoundary presenter = new CreateRecipePresenter(viewModel, viewManagerModel);
            CreateRecipeInputBoundary interactor = new CreateRecipeInteractor(recipeDAO, userSavedRecipeDAO, presenter);
            CreateRecipeController controller = new CreateRecipeController(interactor);

            CreateRecipeView view = new CreateRecipeView(
                    viewModel,
                    controller,
                    viewManagerModel,
                    testUserId
            );

            JFrame frame = new JFrame("Create Recipe Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
    }
}