package zerobase.group2.cookingRecipe.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    BEFORE_AUTH("이메일 인증 전"),
    IN_USE("정상 이용 중"),
    BANNED("이용 정지 중"),
    WITHDRAW("탈퇴함");

    private final String description;
}
