package zerobase.group2.cookingRecipe.rating.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RatingInput {

    @NotBlank
    private String recipeId;
    @NotNull
    @Size(min = 1, max = 5)
    private int score;

}
