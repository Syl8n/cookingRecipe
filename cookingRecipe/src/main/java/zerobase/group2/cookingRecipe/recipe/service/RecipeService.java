package zerobase.group2.cookingRecipe.recipe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.dto.RecipeDto;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@Service
@RequiredArgsConstructor
public class RecipeService {

    public static final String DELETED_RECIPE_TITLE = "삭제된 레시피";
    public static final int VIEW_UPDATE_INTERVAL = 60 * 60 * 24;
    public static final String COOKIE_NAME = "recipeView";
    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;

    public RecipeDto createRecipe(String title, String mainImagePathSmall,
        String mainImagePathBig, String type1, String type2, String ingredients,
        double kcal, List<String> manual, List<String> manualImagePath, String email) {

        validateUser(email);

        return RecipeDto.from(recipeRepository.save(
            Recipe.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .title(title)
                .mainImagePathBig(mainImagePathBig)
                .mainImagePathSmall(mainImagePathSmall)
                .type1(type1)
                .type2(type2)
                .ingredients(ingredients)
                .kcal(kcal)
                .manual(manual)
                .manualImagePath(manualImagePath)
                .status(RecipeStatus.REGISTERED)
                .email(email)
                .build())
        );
    }

    public RecipeDto readRecipe(String recipeId, Cookie[] cookies, HttpServletResponse response) {
        Recipe recipe = getRecipeById(recipeId);
        validateRecipe(recipe);

        Cookie cookie = findCookie(cookies);
        updateViews(recipe, cookie, response);

        return RecipeDto.from(recipe);
    }

    private Cookie findCookie(Cookie[] cookies){
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(COOKIE_NAME)) {
                    return c;
                }
            }
        }
        return null;
    }

    private void updateViews(Recipe recipe, Cookie cookie, HttpServletResponse response) {
        if(cookie != null && cookie.getValue().contains("["+ recipe.getId() +"]")){
            return;
        }

        if(cookie == null){
            cookie = new Cookie(COOKIE_NAME, "[" + recipe.getId() + "]");
        } else if (!cookie.getValue().contains("["+ recipe.getId() +"]")){
            cookie.setValue(cookie.getValue() + "_[" + recipe.getId() + "]");
        }

        recipe.setViews(recipe.getViews() + 1);
        recipeRepository.save(recipe);

        cookie.setPath("/");
        cookie.setMaxAge(VIEW_UPDATE_INTERVAL);
        response.addCookie(cookie);
    }

    public String checkAuthorityToEditRecipe(String recipeId, String email) {
        Member member = getUserById(email);
        Recipe recipe = getRecipeById(recipeId);

        validateRecipe(recipe);
        validateEditor(member, recipe);

        return recipe.getId();
    }

    public RecipeDto processEditRecipe(String recipeId, String title, String mainImagePathSmall,
        String mainImagePathBig, String type1, String type2, String ingredients,
        double kcal, List<String> manual, List<String> manualImagePath, String email) {

        Member member = getUserById(email);
        Recipe recipe = getRecipeById(recipeId);

        validateRecipe(recipe);
        validateEditor(member, recipe);

        recipe.setTitle(title);
        recipe.setMainImagePathSmall(mainImagePathSmall);
        recipe.setMainImagePathBig(mainImagePathBig);
        recipe.setType1(type1);
        recipe.setType2(type2);
        recipe.setIngredients(ingredients);
        recipe.setKcal(kcal);
        recipe.setManual(manual);
        recipe.setManualImagePath(manualImagePath);

        return RecipeDto.from(recipeRepository.save(recipe));
    }

    public RecipeDto deleteRecipe(String recipeId, String email) {
        Member member = getUserById(email);
        Recipe recipe = getRecipeById(recipeId);

        validateRecipe(recipe);
        validateEditor(member, recipe);

        recipe.setTitle(DELETED_RECIPE_TITLE);
        recipe.setStatus(RecipeStatus.UNREGISTERED);
        recipe.setDeletedAt(LocalDateTime.now());
        recipe.setMainImagePathSmall("");
        recipe.setMainImagePathBig("");
        recipe.setType1("");
        recipe.setType2("");
        recipe.setIngredients("");
        recipe.setKcal(0);
        recipe.setManual(null);
        recipe.setManualImagePath(null);

        return RecipeDto.from(recipeRepository.save(recipe));
    }

    private static void validateRecipe(Recipe recipe) {
        if(recipe.getStatus() == RecipeStatus.UNREGISTERED){
            throw new CustomException(ErrorCode.RECIPE_NOT_FOUND);
        }
    }

    private void validateUser(String email) {
        memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateEditor(Member member, Recipe recipe) {
        if (!member.getEmail().equals(recipe.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_EDITOR);
        }
    }

    private Recipe getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }

    private Member getUserById(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
