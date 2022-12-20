package zerobase.group2.cookingRecipe.rating.controller;

import java.security.Principal;
import javax.validation.Valid;
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
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseResult rateRecipe(Principal principal,
        @RequestBody @Valid RatingInput input) {
        return ResponseResult.ok(
            ratingService.rateRecipe(principal.getName(), input.getRecipeId(), input.getScore()));
    }

    @PutMapping
    public ResponseResult updateRateRecipe(Principal principal,
        @RequestBody @Valid RatingInput input) {
        return ResponseResult.ok(
            ratingService.updateRateRecipe(principal.getName(), input.getRecipeId(),
                input.getScore()));
    }
}
