package zerobase.group2.cookingRecipe.recipe.service;

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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.dto.RecipeDto;
import zerobase.group2.cookingRecipe.recipe.dto.RecipeInput;
import zerobase.group2.cookingRecipe.recipe.dto.RecipeInput.Request;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@ExtendWith(SpringExtension.class)
class RecipeServiceTest {

    public static final long VIEWS = 10L;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Member member1;
    private Member member2;

    private Recipe recipe;
    private RecipeInput.Request request;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
            .email("group2@gmail.com")
            .name("g2")
            .build();
        member2 = Member.builder()
            .email("user@gmail.com")
            .name("user")
            .build();
        request = new Request("recipe title",
            "smallImagePath",
            "bigImagePath",
            "구이",
            "반찬",
            "삼겹살",
            300.0,
            Arrays.asList("판을 달군다", "고기를 꺼낸다", "굽는다"),
            Arrays.asList("manualImage1", "manualImage2", "manualImage3")
        );
        recipe = Recipe.builder()
            .id(1L)
            .visualId(UUID.randomUUID().toString().replace("-", ""))
            .title(request.getTitle())
            .mainImagePathBig(request.getMainImagePathBig())
            .mainImagePathSmall(request.getMainImagePathSmall())
            .type1(request.getType1())
            .type2(request.getType2())
            .ingredients(request.getIngredients())
            .kcal(request.getKcal())
            .manual(request.getManual())
            .manualImagePath(request.getManualImagePath())
            .status(RecipeStatus.REGISTERED)
            .email(member1.getEmail())
            .views(VIEWS)
            .build();
    }

    @Test
    @DisplayName("레시피 생성 성공")
    void success_createRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.save(any()))
            .willReturn(recipe);

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);

        //when
        recipeService.createRecipe(
            request.getTitle(),
            request.getMainImagePathSmall(),
            request.getMainImagePathBig(),
            request.getType1(),
            request.getType2(),
            request.getIngredients(),
            request.getKcal(),
            request.getManual(),
            request.getManualImagePath(),
            member1.getEmail()
        );

        //then
        verify(recipeRepository).save(captor.capture());
        Recipe captorValue = captor.getValue();
        assertEquals(request.getTitle(), captorValue.getTitle());
        assertEquals(request.getMainImagePathSmall(), captorValue.getMainImagePathSmall());
        assertEquals(request.getMainImagePathBig(), captorValue.getMainImagePathBig());
        assertEquals(request.getType1(), captorValue.getType1());
        assertEquals(request.getType2(), captorValue.getType2());
        assertEquals(request.getKcal(), captorValue.getKcal());
        assertEquals(request.getManual(), captorValue.getManual());
        assertEquals(request.getManualImagePath(), captorValue.getManualImagePath());
        assertEquals(member1.getEmail(), captorValue.getEmail());
        assertEquals(RecipeStatus.REGISTERED, captorValue.getStatus());
    }

    @Test
    @DisplayName("레시피 생성 실패 - 존재하지 않는 회원")
    void fail_createRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.createRecipe(
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                member1.getEmail()
            )
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 조회 성공 - 조회수 증가")
    void success_readRecipe() {
        //given
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));
        //when
        RecipeDto recipeDto = recipeService.readRecipe("id", httpRequest.getCookies(), httpResponse);

        //then
        assertEquals(recipe.getVisualId(), recipeDto.getVisualId());
        assertEquals(recipe.getTitle(), recipeDto.getTitle());
        assertEquals(recipe.getMainImagePathBig(), recipeDto.getMainImagePathBig());
        assertEquals(recipe.getMainImagePathSmall(), recipeDto.getMainImagePathSmall());
        assertEquals(recipe.getType1(), recipeDto.getType1());
        assertEquals(recipe.getType2(), recipeDto.getType2());
        assertEquals(recipe.getIngredients(), recipeDto.getIngredients());
        assertEquals(recipe.getKcal(), recipeDto.getKcal());
        assertEquals(recipe.getManual(), recipeDto.getManual());
        assertEquals(recipe.getManualImagePath(), recipeDto.getManualImagePath());
        assertEquals(recipe.getEmail(), recipeDto.getEmail());
        assertEquals(VIEWS + 1, recipeDto.getViews());
    }

    @Test
    @DisplayName("레시피 조회 성공 - 조회수 중복 방지")
    void success_readRecipe_viewsNotIncrease() {
        //given
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));
        //when
        Cookie[] cookies = new Cookie[]{new Cookie("recipeView", "[" + recipe.getVisualId() + "]")};
        RecipeDto recipeDto = recipeService.readRecipe("id", cookies, httpResponse);

        //then
        assertEquals(recipe.getVisualId(), recipeDto.getVisualId());
        assertEquals(recipe.getTitle(), recipeDto.getTitle());
        assertEquals(recipe.getMainImagePathBig(), recipeDto.getMainImagePathBig());
        assertEquals(recipe.getMainImagePathSmall(), recipeDto.getMainImagePathSmall());
        assertEquals(recipe.getType1(), recipeDto.getType1());
        assertEquals(recipe.getType2(), recipeDto.getType2());
        assertEquals(recipe.getIngredients(), recipeDto.getIngredients());
        assertEquals(recipe.getKcal(), recipeDto.getKcal());
        assertEquals(recipe.getManual(), recipeDto.getManual());
        assertEquals(recipe.getManualImagePath(), recipeDto.getManualImagePath());
        assertEquals(recipe.getEmail(), recipeDto.getEmail());
        assertEquals(VIEWS, recipeDto.getViews());
    }

    @Test
    @DisplayName("레시피 조회 실패 - 존재하지 않는 레시피")
    void fail_readRecipe_recipeNotFound() {
        //given
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.readRecipe("id", httpRequest.getCookies(), httpResponse)
        );

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 조회 실패 - 삭제된 레시피")
    void fail_readRecipe_recipeUnregistered() {
        //given
        recipe.setStatus(RecipeStatus.UNREGISTERED);
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.readRecipe("id", httpRequest.getCookies(), httpResponse)
        );

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 요청 성공")
    void success_requestEditRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        String recipeId = recipeService.checkAuthorityToEditRecipe("id", "email");

        //then
        assertEquals(recipe.getVisualId(), recipeId);
    }

    @Test
    @DisplayName("레시피 수정 요청 실패 - 존재하지 않는 회원")
    void fail_requestEditRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.checkAuthorityToEditRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 요청 실패 - 존재하지 않는 레시피")
    void fail_requestEditRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.checkAuthorityToEditRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 요청 실패 - 삭제된 레시피")
    void fail_requestEditRecipe_recipeUnregistered() {
        //given
        recipe.setStatus(RecipeStatus.UNREGISTERED);
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.checkAuthorityToEditRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 요청 실패 - 해당 레시피에 대한 수정 권한 없는 유저")
    void fail_requestEditRecipe_userNotEditor() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member2));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.checkAuthorityToEditRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.USER_NOT_EDITOR, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 성공")
    void success_processEditRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));
        given(recipeRepository.save(any()))
            .willReturn(recipe);

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);

        RecipeInput.Request req = new Request(
            "edited title",
            "edited smallImagePath",
            "edited bigImagePath",
            "edited 구이",
            "edited 반찬",
            "edited 삼겹살",
            500.0,
            Arrays.asList("edited 판을 달군다", "고기를 꺼낸다", "굽는다"),
            Arrays.asList("edited manualImage1", "manualImage2", "manualImage3")
        );

        //when
        recipeService.processEditRecipe(
            recipe.getId(),
            req.getTitle(),
            req.getMainImagePathSmall(),
            req.getMainImagePathBig(),
            req.getType1(),
            req.getType2(),
            req.getIngredients(),
            req.getKcal(),
            req.getManual(),
            req.getManualImagePath(),
            member1.getEmail()
        );

        //then
        verify(recipeRepository).save(captor.capture());
        Recipe captorValue = captor.getValue();
        assertEquals(req.getTitle(), captorValue.getTitle());
        assertEquals(req.getMainImagePathSmall(), captorValue.getMainImagePathSmall());
        assertEquals(req.getMainImagePathBig(), captorValue.getMainImagePathBig());
        assertEquals(req.getType1(), captorValue.getType1());
        assertEquals(req.getType2(), captorValue.getType2());
        assertEquals(req.getKcal(), captorValue.getKcal());
        assertEquals(req.getManual(), captorValue.getManual());
        assertEquals(req.getManualImagePath(), captorValue.getManualImagePath());
        assertEquals(member1.getEmail(), captorValue.getEmail());
    }

    @Test
    @DisplayName("레시피 수정 실패 - 존재하지 않는 회원")
    void fail_processEditRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.processEditRecipe(
                recipe.getId(),
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                member1.getEmail()
            )
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 실패 - 존재하지 않는 레시피")
    void fail_processEditRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.processEditRecipe(
                recipe.getId(),
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                member1.getEmail()
            )
        );

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 실패 - 삭제된 레시피")
    void fail_processEditRecipe_recipeUnregistered() {
        //given
        recipe.setStatus(RecipeStatus.UNREGISTERED);
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.processEditRecipe(
                recipe.getId(),
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                member1.getEmail()
            )
        );

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 수정 실패 - 해당 레시피에 대한 수정 권한 없는 유저")
    void fail_processEditRecipe_userNotEditor() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member2));
        given(recipeRepository.findById(anyLong()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.processEditRecipe(
                recipe.getId(),
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                member1.getEmail()
            )
        );

        //then
        assertEquals(ErrorCode.USER_NOT_EDITOR, exception.getError());
    }

    @Test
    @DisplayName("레시피 삭제 성공")
    void success_deleteRecipe() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));
        given(recipeRepository.save(any()))
            .willReturn(recipe);

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);

        //when
        recipeService.deleteRecipe(recipe.getVisualId(), "email");

        //then
        verify(recipeRepository).save(captor.capture());
        assertEquals(RecipeService.DELETED_RECIPE_TITLE, captor.getValue().getTitle());
        assertEquals(RecipeStatus.UNREGISTERED, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("레시피 삭제 실패 - 존재하지 않는 회원")
    void fail_deleteRecipe_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.deleteRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 삭제 실패 - 존재하지 않는 레시피")
    void fail_deleteRecipe_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.deleteRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 삭제 실패 - 이미 삭제된 레시피")
    void fail_deleteRecipe_recipeUnregistered() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member1));
        recipe.setStatus(RecipeStatus.UNREGISTERED);
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.deleteRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("레시피 삭제 실패 - 해당 레시피에 대한 삭제 권한 없는 유저")
    void fail_deleteRecipe_userNotEditor() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member2));
        given(recipeRepository.findByVisualId(anyString()))
            .willReturn(Optional.of(recipe));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
            recipeService.deleteRecipe("id", "email"));

        //then
        assertEquals(ErrorCode.USER_NOT_EDITOR, exception.getError());
    }
}