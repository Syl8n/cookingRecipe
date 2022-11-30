package zerobase.group2.cookingRecipe.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
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
    private MemberRegister.Request request;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .email("group2@gmail.com")
            .name("g2")
            .emailAuthKey(UUID.randomUUID().toString())
            .emailAuthDue(LocalDateTime.now().plusMinutes(1))
            .emailAuthYn(false)
            .status(MemberStatus.BEFORE_AUTH)
            .build();
        request = new MemberRegister.Request("1", "1", "1");
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
        MemberDto memberDto = memberService.register(request);

        //then
        assertEquals(memberDto.getEmail(), member.getEmail());
        assertEquals(memberDto.getName(), member.getName());
        assertEquals(memberDto.getKey(), member.getEmailAuthKey());
    }

    @Test
    @DisplayName("회원가입 실패 - 해당 이메일 이미 사용 중")
    void failedRegister_emailAlreadyInUse() {
        //given
        given(memberRepository.findById(anyString()))
            .willReturn(Optional.of(member));

        //when
        MemberException exception = assertThrows(MemberException.class, () ->
            memberService.register(request));

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
        boolean result = memberService.emailAuth(member.getEmailAuthKey());

        //then
        assertEquals(result, true);
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
        assertEquals(false, member.isEmailAuthYn());
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
        assertEquals(true, LocalDateTime.now().isBefore(member.getEmailAuthDue()));
        assertEquals(true, member.isEmailAuthYn());
        assertEquals(ErrorCode.ACCESS_NOT_VALID, exception.getError());
    }
}