package zerobase.group2.cookingRecipe.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private String email;
    private String name;

    public static RegisterResponse from(MemberDto memberDto){
        return RegisterResponse.builder()
                .email(memberDto.getEmail())
                .name(memberDto.getName())
                .build();
    }
}
