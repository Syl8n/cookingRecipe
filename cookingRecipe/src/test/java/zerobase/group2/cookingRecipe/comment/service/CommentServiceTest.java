package zerobase.group2.cookingRecipe.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
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
import zerobase.group2.cookingRecipe.comment.dto.CommentDto;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.comment.repository.CommentRepository;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    private Member member;
    private Recipe recipe;
    private Comment comment;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("group2@gmail.com")
                .name("g2")
                .status(MemberStatus.IN_USE)
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
                .manualImagePath(Arrays.asList("manualImage1", "manualImage2",
                        "manualImage3"))
                .status(RecipeStatus.REGISTERED)
                .email(member.getEmail())
                .build();
        comment = Comment.builder()
                .id(1L)
                .member(member)
                .recipe(recipe)
                .text("text")
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void success_writeComment() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
                .willReturn(Optional.of(recipe));
        given(commentRepository.save(any()))
                .willReturn(comment);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        //when
        commentService.writeComment("email", 1, "text");

        //then
        verify(commentRepository).save(captor.capture());
        Comment captorValue = captor.getValue();
        assertEquals(member.getEmail(), captorValue.getMember().getEmail());
        assertEquals(recipe.getTitle(), captorValue.getRecipe().getTitle());
        assertEquals(comment.getText(), captorValue.getText());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 회원")
    void fail_writeComment_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                commentService.writeComment("email", 1, "text"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 레시피")
    void fail_writeComment_recipeNotFound() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.of(member));
        given(recipeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                commentService.writeComment("email", 1, "text"));

        //then
        assertEquals(ErrorCode.RECIPE_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void success_updateComment() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.of(member));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));
        comment.setText("text2");
        given(commentRepository.save(any()))
                .willReturn(comment);

        //when
        CommentDto dto = commentService.updateComment("email", 1, "text");

        //then
        assertEquals(member.getEmail(), dto.getMemberEmail());
        assertEquals(recipe.getTitle(), dto.getRecipeTitle());
        assertEquals(comment.getText(), dto.getText());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 회원")
    void fail_updateComment_userNotFound() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                commentService.updateComment("email", 1, "text"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 코멘트")
    void fail_updateComment_commentNotFound() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.of(member));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                commentService.updateComment("email", 1, "text"));

        //then
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void success_deleteComment() {
        //given
        given(memberRepository.findById(anyString()))
                .willReturn(Optional.of(member));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(comment));

        //when
        commentService.deleteComment("email", 1);

        //then
        verify(commentRepository).delete(comment);
    }
}