package zerobase.group2.cookingRecipe.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.exception.MemberException;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.type.ErrorCode;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MailComponent mailComponent;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .email("group2@gmail.com")
            .name("g2")
            .emailAuthKey(UUID.randomUUID().toString())
            .emailAuthDue(LocalDateTime.now().plusMinutes(1))
            .emailAuthYn(false)
            .status(MemberStatus.BEFORE_AUTH)
            .registeredAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }


    @Test
    @DisplayName("회원가입 성공")
    void successRegister() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());
        given(memberRepository.save(any()))
            .willReturn(member);
        given(mailComponent.sendMail(anyString(), anyString(), anyString()))
            .willReturn(true);

        //when
        MemberDto memberDto = memberService.register("1", "1", "1");

        //then
        assertEquals(memberDto.getEmail(), member.getEmail());
        assertEquals(memberDto.getName(), member.getName());
    }

    @Test
    @DisplayName("회원가입 실패 - 해당 이메일 이미 사용 중")
    void failedRegister_emailAlreadyInUse() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));

        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.register("1", "1", "1"));

        //then
        assertEquals(ErrorCode.EMAIL_ALREADY_REGISTERED, exception.getError());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void successEmailAuth() {
        //given
        given(memberRepository.findByEmailAuthKey(anyString()))
            .willReturn(Optional.of(member));
        //when
        memberService.emailAuth(member.getEmailAuthKey());

        //then
        assertTrue(member.isEmailAuthYn());
        assertEquals(MemberStatus.IN_USE, member.getStatus());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 해당 인증키 없음")
    void failedEmailAuth_keyNotFound() {
        //given

        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.emailAuth("1"));
        //then
        assertEquals(ErrorCode.DATA_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증키 유효기간 초과")
    void failedEmailAuth_keyExpired() {
        //given
        member.setEmailAuthDue(LocalDateTime.now().minusSeconds(1));
        given(memberRepository.findByEmailAuthKey(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.emailAuth(member.getEmailAuthKey()));
        //then
        assertFalse(member.isEmailAuthYn());
        assertEquals(ErrorCode.ACCESS_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 이미 인증된 계정")
    void failedEmailAuth_alreadyAuthenticated() {
        //given
        member.setEmailAuthYn(true);
        given(memberRepository.findByEmailAuthKey(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.emailAuth(member.getEmailAuthKey()));
        //then
        assertTrue(LocalDateTime.now().isBefore(member.getEmailAuthDue()));
        assertTrue(member.isEmailAuthYn());
        assertEquals(ErrorCode.ACCESS_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("유저 정보 조회 성공")
    void success_getInfoById() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberDto memberDto = memberService.getInfoById("");
        //then
        assertEquals(member.getEmail(), memberDto.getEmail());
        assertEquals(member.getName(), memberDto.getName());
        assertEquals(member.getRegisteredAt(), memberDto.getRegisteredAt());
        assertEquals(member.getUpdatedAt(), memberDto.getUpdatedAt());
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 존재하지 않는 회원")
    void fail_getInfoById() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.empty());
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.getInfoById("1"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void success_editMemberInfo() {
        //given
        String name = "이거절대사람이름아님";
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberDto memberDto = memberService.editMemberInfo("1", name);
        //then
        assertEquals(name, memberDto.getName());
    }

    @Test
    @DisplayName("유저 정보 수정 실패 - 존재하지 않는 회원")
    void fail_editMemberInfo() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.editMemberInfo("1", "name"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    void success_editPassword() {
        //given
        String oldPassword = "1111";
        String newPassword = "2222";
        member.setPassword(BCrypt.hashpw(oldPassword, BCrypt.gensalt()));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        memberService.editPassword("1", oldPassword, newPassword);
        //then
        assertEquals(BCrypt.hashpw(newPassword, member.getPassword()), member.getPassword());
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 존재하지 않는 회원")
    void fail_editPassword_userNotFound() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.editPassword("1", "1", "2"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 비밀번호 불일치")
    void fail_editPassword_passwordNotMatched() {
        //given
        member.setPassword(BCrypt.hashpw("3333", BCrypt.gensalt()));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.editPassword("1", "1111", "2222"));
        //then
        assertEquals(ErrorCode.DATA_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void success_withdraw() {
        //given
        member.setPassword(BCrypt.hashpw("1111", BCrypt.gensalt()));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        memberService.withdraw("1", "1111");
        //then
        assertEquals("탈퇴회원", member.getName());
        assertEquals(MemberStatus.WITHDRAW, member.getStatus());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
    void fail_withdraw_userNotFound() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.withdraw("1", "1111"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void fail_withdraw_passwordNotMatched() {
        //given
        member.setPassword(BCrypt.hashpw("2222", BCrypt.gensalt()));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.withdraw("1", "1111"));
        //then
        assertEquals(ErrorCode.DATA_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 초기화 키 발급 성공")
    void success_sendResetEmail() {
        //given
        member.setPasswordResetKey("");
        member.setPasswordResetDue(LocalDateTime.now().minusDays(1));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        boolean result = memberService.sendEmailToResetPassword("1");
        //then
        assertTrue(result);
        assertTrue(LocalDateTime.now().isBefore(member.getPasswordResetDue()));
        assertNotEquals("", member.getPasswordResetKey());
    }

    @Test
    @DisplayName("비밀번호 초기화 키 발급 실패 - 존재하지 않는 회원")
    void fail_sendResetEmail_userNotFound() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.sendEmailToResetPassword("1"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 초기화 키 발급 실패 - 유효한 키 이미 존재")
    void fail_sendResetEmail_keyAlreadyIssued() {
        //given
        member.setPasswordResetKey(UUID.randomUUID().toString());
        member.setPasswordResetDue(LocalDateTime.now().plusHours(1));
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.sendEmailToResetPassword("1"));
        //then
        assertEquals(ErrorCode.ACCESS_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 초기화 페이지 접근 성공")
    void success_resetAuth() {
        //given
        member.setPasswordResetKey("1111");
        member.setEmailAuthYn(false);
        member.setPasswordResetDue(LocalDateTime.now().plusHours(1));
        given(memberRepository.findByPasswordResetKey(anyString()))
            .willReturn(Optional.of(member));
        //when
        String email = memberService.authPasswordResetKey("1");

        //then
        assertEquals(member.getEmail(), email);
        assertEquals("", member.getPasswordResetKey());
        assertTrue(LocalDateTime.now().plusMinutes(1).isAfter(member.getPasswordResetDue()));
        assertTrue(member.isEmailAuthYn());
    }

    @Test
    @DisplayName("비밀번호 초기화 페이지 접근 실패 - 유효하지 않은 키")
    void fail_resetAuth_NotValidUrl() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.authPasswordResetKey("1"));
        //then
        assertEquals(ErrorCode.DATA_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 초기화 페이지 접근 실패 - 키 기간 만료")
    void fail_resetAuth_KeyExpired() {
        //given
        member.setPasswordResetDue(LocalDateTime.now().minusSeconds(1));
        given(memberRepository.findByPasswordResetKey(anyString()))
            .willReturn(Optional.of(member));
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.authPasswordResetKey("1"));
        //then
        assertEquals(ErrorCode.ACCESS_NOT_VALID, exception.getError());
    }

    @Test
    @DisplayName("비밀번호 초기화 성공")
    void success_resetProcess() {
        //given
        member.setPassword("1111");
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));
        //when
        memberService.processResetPassword("1", "1111");
        //then
        assertNotEquals("1111", member.getPassword());
    }

    @Test
    @DisplayName("비밀번호 초기화 실패 - 존재하지 않는 회원")
    void fail_resetProcess_userNotFound() {
        //given
        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.processResetPassword("1", "1111"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
    }

}