package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class MemberRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Size(min = 11, max = 25)
        private String email;

        @NotNull
        @Size(min = 8, max = 25)
        private String password;

        @NotNull
        @Size(min = 2)
        private String name;
    }
}
