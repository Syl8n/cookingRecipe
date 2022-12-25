package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String key;

}
