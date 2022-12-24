package zerobase.group2.cookingRecipe.comment.controller;

import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.comment.dto.CommentInput;
import zerobase.group2.cookingRecipe.comment.service.CommentService;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseResult writeComment(Principal principal,
        @RequestBody @Valid CommentInput.Write input) {
        return ResponseResult.ok(commentService.writeComment(principal.getName(),
            input.getRecipeId(), input.getText()));
    }

    @PutMapping
    public ResponseResult updateComment(Principal principal,
        @RequestBody @Valid CommentInput.Update input) {
        return ResponseResult.ok(
            commentService.updateComment(principal.getName(), input.getCommentId(),
                input.getText()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(Principal principal,
        @PathVariable long commentId) {
        return ResponseResult.ok(commentService.deleteComment(principal.getName(),
            commentId));
    }
}
