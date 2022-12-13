package zerobase.group2.cookingRecipe.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zerobase.group2.cookingRecipe.api.utils.ApiUtil;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

@Component
@RequiredArgsConstructor
public class OpenApiRecipeClient {
    private final RecipeRepository recipeRepository;

    @Value("${api-key}")
    private String apiKey;

    public void getRecipeFromApi() {
        JSONArray recipeJson = ApiUtil.getRecipeString(apiKey);
        List<Recipe> recipes = ApiUtil.parseRecipe(recipeJson);
        recipeRepository.saveAll(recipes);
    }


}
