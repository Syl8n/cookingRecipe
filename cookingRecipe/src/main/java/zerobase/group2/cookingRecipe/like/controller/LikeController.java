package zerobase.group2.cookingRecipe.like.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{recipeId}")
    public ResponseResult likeRecipe(@PathVariable String recipeId,
        Principal principal){
        return ResponseResult.ok(likeService.likeRecipe(recipeId, principal.getName()));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseResult dislikeRecipe(@PathVariable String recipeId,
        Principal principal){
        return ResponseResult.ok(likeService.dislikeRecipe(recipeId, principal.getName()));
    }
}
