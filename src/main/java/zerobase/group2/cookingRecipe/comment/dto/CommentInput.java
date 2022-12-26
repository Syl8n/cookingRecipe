package zerobase.group2.cookingRecipe.comment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class CommentInput {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Write {
        @NotNull
        @ApiModelProperty("레시피 실제 ID (테스트용 220~230)")
        private Long recipeId;
        @NotBlank
        @Length(max = 500)
        @ApiModelProperty("댓글 텍스트")
        private String text;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Update {
        @NotNull
        @ApiModelProperty("코멘트 ID")
        private long commentId;
        @NotBlank
        @Length(max = 500)
        @ApiModelProperty("댓글 텍스트")
        private String text;
    }
}
