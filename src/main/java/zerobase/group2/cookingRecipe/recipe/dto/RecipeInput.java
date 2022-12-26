package zerobase.group2.cookingRecipe.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RecipeInput {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{
        @NotBlank
        @ApiModelProperty("레시피 제목")
        private String title;

        @ApiModelProperty("목록 표시용 이미지 경로")
        private String mainImagePathSmall;

        @ApiModelProperty("상세 페이지 표시용 이미지 경로")
        private String mainImagePathBig;
        @NotBlank
        @ApiModelProperty("조리방법 e.g. 굽기, 끓이기")
        private String type1;
        @NotBlank
        @ApiModelProperty("요리 분류 e.g. 밥, 반찬")
        private String type2;

        @NotBlank
        @ApiModelProperty("재료")
        private String ingredients;
        @NotNull
        @ApiModelProperty("열량")
        private double kcal;

        @NotNull
        @ApiModelProperty("조리법")
        private List<String> manual;
        @NotNull
        @ApiModelProperty("조리법 이미지 경로")
        private List<String> manualImagePath;

        @ApiModelProperty("정수형 실제 ID")
        private Long id;
    }
}
