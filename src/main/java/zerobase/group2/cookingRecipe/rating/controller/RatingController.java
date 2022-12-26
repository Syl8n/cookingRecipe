package zerobase.group2.cookingRecipe.rating.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.rating.dto.RatingInput;
import zerobase.group2.cookingRecipe.rating.service.RatingService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rating")
@Api(tags = {"평점 부여/수정 api"})
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ApiOperation(value = "평점을 부여합니다",
            notes = "주소창에 보이는 레시피 ID는 실 ID를 감추기 위한 용이라 이 API의 Input으로 사용되지는 못합니다. " +
                    "테스트용으로 220~230 정도의 값을 ID로 줄 수 있습니다")
    public ResponseResult rateRecipe(Principal principal,
        @RequestBody @Valid RatingInput input) {
        return ResponseResult.ok(
            ratingService.rateRecipe(principal.getName(), input.getRecipeId(), input.getScore()));
    }

    @PutMapping
    @ApiOperation(value = "평점을 수정합니다",
            notes = "주소창에 보이는 레시피 ID는 실 ID를 감추기 위한 용이라 이 API의 Input으로 사용되지는 못합니다. " +
                    "테스트용으로 220~230 정도의 값을 ID로 줄 수 있습니다")
    public ResponseResult updateRateRecipe(Principal principal,
        @RequestBody @Valid RatingInput input) {
        return ResponseResult.ok(
            ratingService.updateRateRecipe(principal.getName(), input.getRecipeId(),
                input.getScore()));
    }
}
