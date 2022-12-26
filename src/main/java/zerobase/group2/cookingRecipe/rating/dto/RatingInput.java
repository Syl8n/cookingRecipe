package zerobase.group2.cookingRecipe.rating.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class RatingInput {

    @NotNull
    @ApiModelProperty("레시피 ID")
    private Long recipeId;
    @NotNull
    @Min(1)
    @Max(5)
    @ApiModelProperty("평점 (0~5)")
    private int score;

}
