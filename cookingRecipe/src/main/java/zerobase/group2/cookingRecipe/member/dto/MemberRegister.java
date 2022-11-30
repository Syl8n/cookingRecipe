package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class MemberRegister {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String name;
    }

}
