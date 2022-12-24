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
    USER_NOT_EDITOR("편집 관한이 없습니다."),
    RECIPE_ALREADY_LIKED("이미 좋아요를 한 레시피입니다."),
    RECIPE_NOT_LIKED("좋아요를 하지 않은 레시피입니다."),
    COMMENT_NOT_FOUND("댓글 정보가 존재하지 않습니다."),
    RATING_NOT_FOUND("평점 정보가 존재하지 않습니다."),
    EMAIL_NOT_AUTHENTICATED("이메일 인증이 되지 않았습니다."),
    TOKEN_NOT_VALID("유효하지 않은 토큰입니다."),
    RECIPE_ALREADY_RATED("이미 평가한 레시피입니다.")
;
    private final String description;
}
