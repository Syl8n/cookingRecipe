package zerobase.group2.cookingRecipe.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    public CommentDto writeComment(String email, long recipeId, String text) {
        Member member = getMemberById(email);
        Recipe recipe = getRecipeById(recipeId);

        return CommentDto.from(commentRepository.save(
            Comment.builder()
                .member(member)
                .recipe(recipe)
                .text(text)
                .build()
        ));
    }

    public CommentDto updateComment(String email, long commentId, String text) {
        Member member = getMemberById(email);
        Comment comment = getCommentById(commentId);

        validateCommentEditor(member, comment.getMember());

        comment.setText(text);

        return CommentDto.from(commentRepository.save(comment));
    }

    public CommentDto deleteComment(String email, long commentId) {
        getMemberById(email);
        Comment comment = getCommentById(commentId);

        commentRepository.delete(comment);

        return CommentDto.from(comment);
    }

    private void validateCommentEditor(Member member, Member commentWriter) {
        if(!member.getEmail().equals(commentWriter.getEmail())){
            throw new CustomException(ErrorCode.USER_NOT_EDITOR);
        }
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
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

    private Recipe getRecipeById(long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> new CustomException(ErrorCode.RECIPE_NOT_FOUND));
    }
}
