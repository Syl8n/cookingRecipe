package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String email;
        private String name;

        public static Response from(MemberDto memberDto){
            return Response.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .build();
        }
    }
}
