package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @ApiModelProperty("이메일")
    private String email;

    @NotBlank
    @ApiModelProperty("비밀번호")
    private String password;

    @NotBlank
    @ApiModelProperty("닉네임")
    private String name;
}
