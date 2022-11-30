package zerobase.group2.cookingRecipe.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.member.entity.Member;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    String email;
    String name;
    String key;

    public static MemberDto from(Member member){
        return MemberDto.builder()
            .email(member.getEmail())
            .name(member.getName())
            .key(member.getEmailAuthKey())
            .build();
    }
}
