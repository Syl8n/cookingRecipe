package zerobase.group2.cookingRecipe.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_REGISTERED("이미 등록된 이메일입니다"),
    DATA_NOT_VALID("올바르지 않은 정보입니다"),
    ACCESS_NOT_VALID("올바르지 않은 접근입니다")
;
    private final String description;
}
