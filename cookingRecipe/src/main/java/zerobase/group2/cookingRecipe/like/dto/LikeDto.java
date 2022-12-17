package zerobase.group2.cookingRecipe.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {
    private String memberEmail;
    private String recipeTitle;

    public static LikeDto from(LikeEntity like){
        return LikeDto.builder()
            .memberEmail(like.getMember().getEmail())
            .recipeTitle(like.getRecipe().getTitle())
            .build();
    }
}
