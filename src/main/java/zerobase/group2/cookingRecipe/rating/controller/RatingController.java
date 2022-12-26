package zerobase.group2.cookingRecipe.rating.controller;

import java.security.Principal;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.rating.dto.RatingInput;
import zerobase.group2.cookingRecipe.rating.service.RatingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rating")
@Api("평점 부여 api")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ApiOperation(value = "평점을 부여합니다",
            notes = "주소창에 보이는 레시피 ID는 실 ID를 감추기 위한 용이라 이 API의 Input으로 사용되지는 못합니다. " +
                    "테스트용으로 1~200 정도의 값을 ID로 줄 수 있습니다")
    public ResponseResult rateRecipe(Principal principal,
        @RequestBody @Valid @ApiParam("레시피 ID, 평점(0~5)") RatingInput input) {
        return ResponseResult.ok(
            ratingService.rateRecipe(principal.getName(), input.getRecipeId(), input.getScore()));
    }

    @PutMapping
    @ApiOperation(value = "평점을 수정합니다",
            notes = "주소창에 보이는 레시피 ID는 실 ID를 감추기 위한 용이라 이 API의 Input으로 사용되지는 못합니다. " +
                    "테스트용으로 1~200 정도의 값을 ID로 줄 수 있습니다")
    public ResponseResult updateRateRecipe(Principal principal,
        @RequestBody @Valid @ApiParam("레시피 ID, 평점(0~5)") RatingInput input) {
        return ResponseResult.ok(
            ratingService.updateRateRecipe(principal.getName(), input.getRecipeId(),
                input.getScore()));
    }
}
