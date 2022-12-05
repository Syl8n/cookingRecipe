package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class EditMemberInfo {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotBlank
        private String name;
    }

}
