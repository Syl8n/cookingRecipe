package zerobase.group2.cookingRecipe.like.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.like.service.LikeService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@Api("찜 추가/제거용 api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{recipeId}")
    @ApiOperation("해당 레시피를 찜 목록에 등록합니다")
    public ResponseResult likeRecipe(@PathVariable
                                     @ApiParam("레시피 ID. 테스트용으로 1~200 가능합니다")
                                     long recipeId, Principal principal) {
        return ResponseResult.ok(
                likeService.likeRecipe(recipeId, principal.getName()));
    }

    @DeleteMapping("/{recipeId}")
    @ApiOperation("해당 레시피를 찜 목록에서 삭제합니다")
    public ResponseResult dislikeRecipe(@PathVariable
                                        @ApiParam("레시피 ID. 테스트용으로 1~200 가능합니다")
                                        long recipeId, Principal principal) {
        return ResponseResult.ok(
                likeService.dislikeRecipe(recipeId, principal.getName()));
    }
}
