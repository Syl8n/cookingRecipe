package zerobase.group2.cookingRecipe.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.like.repository.LikeRepository;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@ExtendWith(SpringExtension.class)
class LikeServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    LikeRepository likeRepository;

    @InjectMocks
    LikeService likeService;

    private Member member;
    private Recipe recipe;
    private LikeEntity like;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .email("group2@gmail.com")
            .name("g2")
            .build();
        recipe = Recipe.builder()
            .id(UUID.randomUUID().toString().replace("-", ""))
            .title("recipe title")
            .mainImagePathBig("bigImagePath")
            .mainImagePathSmall("smallImagePath")
            .type1("구이")
            .type2("반찬")
            .ingredients("삼겹살")
            .kcal(300.0)
            .manual(Arrays.asList("판을 달군다", "고기를 꺼낸다", "굽는다"))
            .manualImagePath(Arrays.asList("manualImage1", "manualImage2", "manualImage3"))
            .status(RecipeStatus.REGISTERED)
            .email(member.getEmail())
            .likeCount(10)
            .build();
        like = LikeEntity.builder()
            .member(member)
            .recipe(recipe)
            .build();
    }

    @Test
    @DisplayName("좋아요 추가 성공")
    void successLikeRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.of(recipe));
        given(likeRepository.save(any()))
            .willReturn(like);
        ArgumentCaptor<LikeEntity> captor = ArgumentCaptor.forClass(LikeEntity.class);
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        //when
        likeService.likeRecipe("recipeId", "memberEmail");

        //then
        verify(likeRepository).save(captor.capture());
        verify(recipeRepository).save(recipeCaptor.capture());
        LikeEntity captorValue = captor.getValue();
        assertEquals(member.getEmail(), captorValue.getMember().getEmail());
        assertEquals(recipe.getTitle(), captorValue.getRecipe().getTitle());
        assertEquals(11, captorValue.getRecipe().getLikeCount());
        assertEquals(recipeCaptor.getValue().getLikeCount(), captorValue.getRecipe().getLikeCount());
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 존재하지 않는 회원")
    void fail_likeRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.likeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 존재하지 않는 레시피")
    void fail_likeRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.empty());
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.likeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 이미 좋아요 한 레시피")
    void fail_likeRecipe_recipeAlreadyLiked() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.of(recipe));
        given(likeRepository.existsByMemberAndRecipe(any(), any()))
            .willReturn(true);
        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.likeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.RECIPE_ALREADY_LIKED, exception.getError());
    }

    @Test
    @DisplayName("좋아요 삭제 성공")
    void success_dislikeRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.of(recipe));
        given(likeRepository.findByMemberAndRecipe(any(), any()))
            .willReturn(Optional.of(like));

        //when
        likeService.dislikeRecipe("recipeId", "memberEmail");

        //then
        verify(likeRepository).delete(like);
        assertEquals(9, recipe.getLikeCount());
        assertEquals(recipe.getLikeCount(), recipe.getLikeCount());
    }

    @Test
    @DisplayName("좋아요 삭제 실패 - 존재하지 않는 회원")
    void fail_dislikeRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.dislikeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("좋아요 삭제 실패 - 존재하지 않는 레시피")
    void fail_dislikeRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.dislikeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("좋아요 삭제 실패 - 좋아요 상태가 아닌 레시피")
    void fail_dislikeRecipe_recipeNotLiked() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyString()))
            .willReturn(Optional.of(recipe));
        given(likeRepository.findByMemberAndRecipe(any(), any()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.dislikeRecipe("recipeId", "memberEmail"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_LIKED, exception.getError());
    }
}