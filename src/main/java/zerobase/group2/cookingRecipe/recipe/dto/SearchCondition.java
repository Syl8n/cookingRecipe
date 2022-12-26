package zerobase.group2.cookingRecipe.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchCondition {

    @ApiModelProperty("제목 기반 검색어 e.g. 닭가슴살")
    private String title;

    @ApiModelProperty("조리방법 e.g. 끓이기")
    private String type1;

    @ApiModelProperty("요리분류 e.g. 밥")
    private String type2;

}
