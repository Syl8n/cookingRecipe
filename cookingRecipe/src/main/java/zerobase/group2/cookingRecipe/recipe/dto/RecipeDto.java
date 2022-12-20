package zerobase.group2.cookingRecipe.recipe.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;
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

    private List<String> manual;
    private List<String> manualImagePath;

    private Long views;

    private String email;

    private List<LikeEntity> likeEntityList;
    private long likeCount;

    private List<Comment> commentList;

    private List<Rating> ratingList;
    private long totalScore;
    private long ratingCount;

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
            .views(recipe.getViews())
            .email(recipe.getEmail())
            .likeEntityList(recipe.getLikeEntityList())
            .likeCount(recipe.getLikeCount())
            .ratingList(recipe.getRatingList())
            .totalScore(recipe.getTotalScore())
            .ratingCount(recipe.getRatingCount())
            .build();
    }
}
