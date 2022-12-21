package zerobase.group2.cookingRecipe.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.like.dto.LikeDto;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.like.repository.LikeRepository;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    public LikeDto likeRecipe(String recipeId, String email) {

        Member member = getMemberById(email);
        Recipe recipe = getRecipeById(recipeId);

        if(likeRepository.existsByMemberAndRecipe(member, recipe)){
            throw new CustomException(ErrorCode.RECIPE_ALREADY_LIKED);
        }

        LikeEntity like = likeRepository.save(LikeEntity.builder()
            .member(member)
            .recipe(recipe)
            .build());

        recipe.setLikeCount(recipe.getLikeCount() + 1);
        recipeRepository.save(recipe);

        return LikeDto.from(like);
    }

    public LikeDto dislikeRecipe(String recipeId, String email) {
        Member member = getMemberById(email);
        Recipe recipe = getRecipeById(recipeId);

        LikeEntity like = likeRepository.findByMemberAndRecipe(member, recipe)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_LIKED));

        likeRepository.delete(like);

        recipe.setLikeCount(recipe.getLikeCount() - 1);
        recipeRepository.save(recipe);

        return LikeDto.from(like);
    }

    private Member getMemberById(String email) {
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

    private Recipe getRecipeById(String recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }
}
