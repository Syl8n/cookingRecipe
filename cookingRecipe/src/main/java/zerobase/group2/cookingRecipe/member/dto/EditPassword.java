package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class EditPassword {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotBlank
        private String oldPassword;
        @NotBlank
        private String newPassword;
    }

}
