package zerobase.group2.cookingRecipe.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;
import zerobase.group2.cookingRecipe.rating.repository.RatingRepository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@ExtendWith(SpringExtension.class)
class RatingServiceTest {

    public static final int RATING_COUNT = 10;
    public static final long TOTAL_SCORE = 100L;
    @Mock
    MemberRepository memberRepository;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RatingRepository ratingRepository;

    @InjectMocks
    RatingService ratingService;

    private Member member;
    private Recipe recipe;
    private Rating rating;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .email("group2@gmail.com")
            .name("g2")
            .build();
        recipe = Recipe.builder()
            .visualId(UUID.randomUUID().toString().replace("-", ""))
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
            .totalScore(TOTAL_SCORE)
            .ratingCount(RATING_COUNT)
            .build();
        rating = Rating.builder()
            .member(member)
            .recipe(recipe)
            .score(5)
            .build();
    }

    @Test
    @DisplayName("평가 성공")
    void success_rateRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));
        given(ratingRepository.save(any()))
            .willReturn(rating);
        ArgumentCaptor<Rating> captor = ArgumentCaptor.forClass(Rating.class);

        //when
        ratingService.rateRecipe("email", 1L, rating.getScore());

        //then
        verify(ratingRepository).save(captor.capture());
        Rating captorValue = captor.getValue();
        assertEquals(member.getEmail(), captorValue.getMember().getEmail());
        assertEquals(recipe.getTitle(), captorValue.getRecipe().getTitle());
        assertEquals(rating.getScore(), captorValue.getScore());
        assertEquals(TOTAL_SCORE + rating.getScore(), captorValue.getRecipe().getTotalScore());
        assertEquals(RATING_COUNT + 1, captorValue.getRecipe().getRatingCount());
    }

    @Test
    @DisplayName("레시피 평가 실패 - 존재하지 않는 회원")
    void fail_rateRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            ratingService.rateRecipe("e", 1L, 1));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 평가 실패 - 존재하지 않는 레시피")
    void fail_rateRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            ratingService.rateRecipe("e", 1L, 1));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("평가 수정 성공")
    void success_updateRateRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));
        given(ratingRepository.findByMemberAndRecipe(any(), any()))
            .willReturn(Optional.of(rating));
        given(ratingRepository.save(any()))
            .willReturn(rating);
        ArgumentCaptor<Rating> captor = ArgumentCaptor.forClass(Rating.class);

        //when
        ratingService.updateRateRecipe("email", 1L, 1);

        //then
        verify(ratingRepository).save(captor.capture());
        Rating captorValue = captor.getValue();
        assertEquals(member.getEmail(), captorValue.getMember().getEmail());
        assertEquals(recipe.getTitle(), captorValue.getRecipe().getTitle());
        assertEquals(rating.getScore(), captorValue.getScore());
        assertEquals(TOTAL_SCORE - 4, captorValue.getRecipe().getTotalScore());
    }

    @Test
    @DisplayName("평가 수정 실패 - 존재하지 않는 회원")
    void fail_updateRateRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            ratingService.updateRateRecipe("e", 1L, 1));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("평가 수정 실패 - 존재하지 않는 레시피")
    void fail_updateRateRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            ratingService.updateRateRecipe("e", 1L, 1));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("평가 수정 실패 - 존재하지 않는 평가")
    void fail_updateRateRecipe_ratingNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));
        given(ratingRepository.findByMemberAndRecipe(any(), any()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            ratingService.updateRateRecipe("e", 1L, 1));

        //then
        assertEquals(ErrorCode.RATING_NOT_FOUND, exception.getError());
    }
}