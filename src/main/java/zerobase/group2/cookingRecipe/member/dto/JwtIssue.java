package zerobase.group2.cookingRecipe.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtIssue {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
