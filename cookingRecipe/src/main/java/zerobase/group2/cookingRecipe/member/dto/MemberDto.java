package zerobase.group2.cookingRecipe.member.dto;

import java.time.LocalDateTime;
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
    private String email;
    private String name;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public static MemberDto from(Member member){
        return MemberDto.builder()
            .email(member.getEmail())
            .name(member.getName())
            .registeredAt(member.getRegisteredAt())
            .updatedAt(member.getUpdatedAt())
            .build();
    }
}
