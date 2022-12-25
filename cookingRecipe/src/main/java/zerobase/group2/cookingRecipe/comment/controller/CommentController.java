package zerobase.group2.cookingRecipe.comment.controller;

import java.security.Principal;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.comment.dto.CommentInput;
import zerobase.group2.cookingRecipe.comment.service.CommentService;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Api("댓글용 api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ApiOperation("댓글을 작성합니다")
    public ResponseResult writeComment(Principal principal,
        @RequestBody @Valid
        @ApiParam("레시피 ID (테스트용 1~200), 댓글 텍스트") CommentInput.Write input) {
        return ResponseResult.ok(commentService.writeComment(principal.getName(),
            input.getRecipeId(), input.getText()));
    }

    @PutMapping
    @ApiOperation("댓글을 수정합니다")
    public ResponseResult updateComment(Principal principal,
        @RequestBody @Valid
        @ApiParam("코멘트 ID, 댓글 텍스트") CommentInput.Update input) {
        return ResponseResult.ok(
            commentService.updateComment(principal.getName(), input.getCommentId(),
                input.getText()));
    }

    @DeleteMapping("/{commentId}")
    @ApiOperation("댓글을 삭제합니다")
    public ResponseResult deleteComment(Principal principal,
        @PathVariable @ApiParam("코멘트 ID") long commentId) {
        return ResponseResult.ok(commentService.deleteComment(principal.getName(),
            commentId));
    }
}
