package zerobase.group2.cookingRecipe.rating.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RatingInput {

    @NotNull
    private Long recipeId;
    @NotNull
    @Min(1)
    @Max(5)
    private int score;

}
