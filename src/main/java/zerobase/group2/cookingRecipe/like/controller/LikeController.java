package zerobase.group2.cookingRecipe.like.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.like.dto.LikeRequest;
import zerobase.group2.cookingRecipe.like.service.LikeService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@Api(tags = {"찜 추가/제거용 api"})
public class LikeController {

    private final LikeService likeService;

    @PostMapping()
    @ApiOperation("해당 레시피를 찜 목록에 등록합니다")
    public ResponseResult likeRecipe(@RequestBody LikeRequest request,
                                     Principal principal) {
        return ResponseResult.ok(
                likeService.likeRecipe(request.getRecipeId(), principal.getName()));
    }

    @DeleteMapping("/{visualId}")
    @ApiOperation("해당 레시피를 찜 목록에서 삭제합니다")
    public ResponseResult dislikeRecipe(@PathVariable
                                        @ApiParam("문자열 ID")
                                        String visualId, Principal principal) {
        return ResponseResult.ok(
                likeService.dislikeRecipe(visualId, principal.getName()));
    }
}
