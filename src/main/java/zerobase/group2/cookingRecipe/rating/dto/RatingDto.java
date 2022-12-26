package zerobase.group2.cookingRecipe.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private String memberEmail;
    private String recipeTitle;
    private int score;

    public static RatingDto from(Rating rating){
        return RatingDto.builder()
            .memberEmail(rating.getMember().getEmail())
            .recipeTitle(rating.getRecipe().getTitle())
            .score(rating.getScore())
            .build();
    }
}
