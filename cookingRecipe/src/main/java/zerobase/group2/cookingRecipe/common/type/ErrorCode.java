package zerobase.group2.cookingRecipe.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("회원 정보가 존재하지 않습니다."),
    EMAIL_ALREADY_REGISTERED("이미 등록된 이메일입니다."),
    DATA_NOT_VALID("올바르지 않은 정보입니다."),
    ACCESS_NOT_VALID("올바르지 않은 접근입니다."),
    RECIPE_NOT_FOUND("레시피 정보가 존재하지 않습니다."),
    USER_NOT_EDITOR("편집 관한이 없습니다.")
;
    private final String description;
}
