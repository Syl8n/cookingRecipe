package zerobase.group2.cookingRecipe.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchCondition {

    private String title;
    private String type1;
    private String type2;

}
