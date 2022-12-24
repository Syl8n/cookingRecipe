package zerobase.group2.cookingRecipe.recipe.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
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

        validateWriter(email);

        return RecipeDto.from(recipeRepository.save(
            Recipe.builder()
                .visualId(UUID.randomUUID().toString().replace("-", ""))
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

    public RecipeDto readRecipe(String visualId, Cookie[] cookies, HttpServletResponse response) {
        Recipe recipe = getRecipeByVisualId(visualId);
        validateRecipe(recipe);

        Cookie cookie = findCookie(cookies);
        updateViews(recipe, cookie, response);

        return RecipeDto.from(recipe);
    }

    public Page<RecipeDto> findRecipesByQuery(String title, String type1, String type2, Pageable pageable){
        if(!StringUtils.hasText(title) && !StringUtils.hasText(type1) && !StringUtils.hasText(type2)){
            return findAllRecipes(pageable);
        }
        return new PageImpl<>(recipeRepository.findAll(createSpecificationFromQuery(title, type1, type2), pageable)
            .stream().map(RecipeDto::from).collect(Collectors.toList()));
    }

    private Page<RecipeDto> findAllRecipes(Pageable pageable){
        return new PageImpl<>(recipeRepository.findAll(pageable).stream()
            .map(RecipeDto::from).collect(Collectors.toList()));
    }

    private Specification<Recipe> createSpecificationFromQuery(String title, String type1, String type2){
        return new Specification<Recipe>() {
            @Override
            public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if(StringUtils.hasText(title)) {
                    list.add(criteriaBuilder.like(root.get("title"), title));
                }
                if(StringUtils.hasText(type1)){
                    list.add(criteriaBuilder.equal(root.get("type1"), type1));
                }
                if(StringUtils.hasText(type2)){
                    list.add(criteriaBuilder.equal(root.get("type2"), type2));
                }
                Predicate finalPredicate = null;
                for(int i = 1; i < list.size(); i++){
                    finalPredicate = criteriaBuilder.and(list.get(i - 1), list.get(i));
                }
                return finalPredicate;
            }
        };
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
        if(cookie != null && cookie.getValue().contains("["+ recipe.getVisualId() +"]")){
            return;
        }

        if(cookie == null){
            cookie = new Cookie(COOKIE_NAME, "[" + recipe.getVisualId() + "]");
        } else if (!cookie.getValue().contains("["+ recipe.getVisualId() +"]")){
            cookie.setValue(cookie.getValue() + "_[" + recipe.getVisualId() + "]");
        }

        recipe.setViews(recipe.getViews() + 1);
        recipeRepository.save(recipe);

        cookie.setPath("/");
        cookie.setMaxAge(VIEW_UPDATE_INTERVAL);
        response.addCookie(cookie);
    }

    public String checkAuthorityToEditRecipe(String visualId, String email) {
        Member member = getUserById(email);
        Recipe recipe = getRecipeByVisualId(visualId);

        validateRecipe(recipe);
        validateEditor(member, recipe);

        return recipe.getVisualId();
    }

    public RecipeDto processEditRecipe(Long recipeId, String title, String mainImagePathSmall,
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

    public RecipeDto deleteRecipe(String visualId, String email) {
        Member member = getUserById(email);
        Recipe recipe = getRecipeByVisualId(visualId);

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

    private void validateWriter(String email) {
        Member member = memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        validateMember(member);
    }

    private void validateEditor(Member member, Recipe recipe) {
        if (!member.getEmail().equals(recipe.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_EDITOR);
        }
    }

    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }

    private Recipe getRecipeByVisualId(String visualId) {
        return recipeRepository.findByVisualId(visualId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }

    private Member getUserById(String email) {
        Member member = memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        validateMember(member);
        return member;
    }

    private void validateMember(Member member) {
        if(member.getStatus() == MemberStatus.BEFORE_AUTH){
            throw new CustomException(ErrorCode.EMAIL_NOT_AUTHENTICATED);
        }
    }

}
