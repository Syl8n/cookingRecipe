package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EditMemberInfoRequest {
    @NotBlank
    private String name;
}
