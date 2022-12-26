package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @ApiModelProperty("이메일")
    private String username;

    @NotBlank
    @ApiModelProperty("비밀번호")
    private String password;
}
