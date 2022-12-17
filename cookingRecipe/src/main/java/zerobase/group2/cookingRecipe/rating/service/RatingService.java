package zerobase.group2.cookingRecipe.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;
import zerobase.group2.cookingRecipe.rating.dto.RatingDto;
import zerobase.group2.cookingRecipe.rating.repository.RatingRepository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    public RatingDto rateRecipe(String email, String recipeId, int score) {
        Member member = getMemberById(email);
        Recipe recipe = getRecipeById(recipeId);

        Rating rating = ratingRepository.save(Rating.builder()
            .member(member)
            .recipe(recipe)
            .score(score)
            .build()
        );

        recipe.setTotalScore(recipe.getTotalScore() + score);
        recipe.setRatingCount(recipe.getRatingCount() + 1);
        recipeRepository.save(recipe);

        return RatingDto.from(rating);
    }

    public RatingDto updateRateRecipe(String email, String recipeId, int newScore) {
        Member member = getMemberById(email);
        Recipe recipe = getRecipeById(recipeId);

        Rating rating = ratingRepository.findByMemberAndRecipe(member, recipe)
            .orElseThrow(() -> new CustomException(ErrorCode.RATING_NOT_FOUND));

        int oldScore = rating.getScore();
        rating.setScore(newScore);
        ratingRepository.save(rating);

        recipe.setTotalScore(recipe.getTotalScore() - oldScore + newScore);
        recipeRepository.save(recipe);

        return RatingDto.from(rating);
    }

    private Member getMemberById(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Recipe getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }
}
