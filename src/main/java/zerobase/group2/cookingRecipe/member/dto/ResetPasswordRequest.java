package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank
    @ApiModelProperty("이메일")
    private String email;
    @NotBlank
    @ApiModelProperty("인증키")
    private String newPassword;
    @NotBlank
    @ApiModelProperty("새 비밀번호")
    private String key;

}
