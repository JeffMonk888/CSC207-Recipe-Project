package usecase.create_recipe;

import data.saved_recipe.RecipeDataAssessObject;
import domain.entity.Ingredient;
import domain.entity.InstructionStep;
import domain.entity.Recipe;
import domain.entity.SavedRecipe;
import usecase.common.MotionForRecipe;

public class CreateRecipeInteractor implements CreateRecipeInputBoundary {

    private final RecipeDataAssessObject recipeDAO;
    private final MotionForRecipe userRecipeDAO;
    private final CreateRecipeOutputBoundary presenter;

    public CreateRecipeInteractor(RecipeDataAssessObject recipeDAO,
                                  MotionForRecipe userRecipeDAO,
                                  CreateRecipeOutputBoundary presenter) {
        this.recipeDAO = recipeDAO;
        this.userRecipeDAO = userRecipeDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(CreateRecipeInputData inputData) {
        if (inputData.getTitle() == null || inputData.getTitle().trim().isEmpty()) {
            presenter.presentFailure("Title cannot be empty.");
            return;
        }

        // 1. 生成基础数字 ID (用于 Recipe 的 id 字段，防止 Long 解析报错)
        long numericId = System.currentTimeMillis();

        // 2. 生成带前缀的 Key (用于 SavedRecipe 的 recipeKey 和 Recipe 的 recipeid)
        String recipeKey = "c" + numericId;

        // 3. 创建 Recipe 对象
        // id 参数传 numericId，apiId 参数传 recipeKey
        Recipe newRecipe = new Recipe(
                numericId,
                inputData.getTitle(),
                "User created recipe",
                1, 0, "User", "", "",
                recipeKey,           // <--- 存入 "c..." 格式
                null
        );

        // 4. 解析食材
        String ingStr = inputData.getIngredients();
        if (ingStr != null && !ingStr.isBlank()) {
            String[] parts = ingStr.split(",");
            for (String part : parts) {
                String name = part.trim();
                if (!name.isEmpty()) {
                    newRecipe.addIngredient(new Ingredient(null, name, null, "", name));
                }
            }
        }

        // 5. 解析步骤
        String instStr = inputData.getInstructions();
        if (instStr != null && !instStr.isBlank()) {
            String[] lines = instStr.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                String step = lines[i].trim();
                if (!step.isEmpty()) {
                    newRecipe.addInstructionStep(new InstructionStep(null, i + 1, step));
                }
            }
        }

        // 6. 保存到 Recipe 数据库 (recipe_cache.json)
        recipeDAO.save(newRecipe);

        // 7. 保存到用户收藏 (user_recipe_links.csv)
        // 适配新的 SavedRecipe 构造函数：接受 String 类型的 key
        SavedRecipe savedLink = new SavedRecipe(inputData.getUserId(), recipeKey);
        userRecipeDAO.save(savedLink);

        // 8. 成功回调
        presenter.presentSuccess(new CreateRecipeOutputData(newRecipe.getTitle(), recipeKey));
    }
}
