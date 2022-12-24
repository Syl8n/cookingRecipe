package zerobase.group2.cookingRecipe.member.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRecipeCompKey implements Serializable {
    private String member;
    private Long recipe;
}
