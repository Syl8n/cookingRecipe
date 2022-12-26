package zerobase.group2.cookingRecipe.recipe.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecipeStatus {
    REGISTERED("등록된 레시피"),
    UNREGISTERED("삭제된 레시피")
    ;

    private final String description;
}
