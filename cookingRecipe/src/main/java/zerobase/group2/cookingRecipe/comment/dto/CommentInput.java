package zerobase.group2.cookingRecipe.comment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


public class CommentInput {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Write {
        @NotBlank
        private String recipeId;
        @NotBlank
        @Length(max = 500)
        private String text;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Update {
        @NotNull
        private long commentId;
        @NotBlank
        @Length(max = 500)
        private String text;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Delete {
        @NotNull
        private long commentId;
    }
}
