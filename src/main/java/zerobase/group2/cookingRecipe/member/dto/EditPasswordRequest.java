package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class EditPasswordRequest {
    @NotBlank
    @ApiModelProperty("이전 비밀번호")
    private String oldPassword;
    @NotBlank
    @ApiModelProperty("새 비밀번호")
    private String newPassword;
}
