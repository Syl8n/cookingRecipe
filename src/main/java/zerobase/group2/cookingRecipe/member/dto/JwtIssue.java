package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtIssue {
    @NotBlank
    @ApiModelProperty("엑세스용 토큰")
    private String accessToken;
    @NotBlank
    @ApiModelProperty("재발급용 토큰")
    private String refreshToken;
}
