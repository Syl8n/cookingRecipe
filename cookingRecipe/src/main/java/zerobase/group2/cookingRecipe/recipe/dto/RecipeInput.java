package zerobase.group2.cookingRecipe.recipe.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RecipeInput {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{
        @NotBlank
        private String title;

        private String mainImagePathSmall;
        private String mainImagePathBig;
        @NotBlank
        private String type1;
        @NotBlank
        private String type2;

        @NotBlank
        private String ingredients;
        @NotNull
        private double kcal;

        @NotNull
        private List<String> manual;
        @NotNull
        private List<String> manualImagePath;
    }
}
