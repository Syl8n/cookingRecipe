package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EditMemberInfoRequest {
    @NotBlank
    @ApiModelProperty("새 닉네임")
    private String name;
}
