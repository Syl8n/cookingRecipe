package zerobase.group2.cookingRecipe.like.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    @ApiModelProperty("레시피 ID (테스트용 220~230)")
    private Long recipeId;
}
