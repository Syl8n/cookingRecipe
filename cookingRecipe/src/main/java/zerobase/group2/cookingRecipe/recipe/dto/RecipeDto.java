package zerobase.group2.cookingRecipe.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {
    private String id;
    private String title;
    private String mainImagePathSmall;
    private String mainImagePathBig;
    private String type1;
    private String type2;

    private String ingredients;
    private double kcal;

    private String manual;
    private String manualImagePath;

    private String email;
    public static RecipeDto from(Recipe recipe){
        return RecipeDto.builder()
            .id(recipe.getId())
            .title(recipe.getTitle())
            .mainImagePathSmall(recipe.getMainImagePathSmall())
            .mainImagePathBig(recipe.getMainImagePathBig())
            .type1(recipe.getType1())
            .type2(recipe.getType2())
            .ingredients(recipe.getIngredients())
            .kcal(recipe.getKcal())
            .manual(recipe.getManual())
            .manualImagePath(recipe.getManualImagePath())
            .email(recipe.getEmail())
            .build();
    }
}
