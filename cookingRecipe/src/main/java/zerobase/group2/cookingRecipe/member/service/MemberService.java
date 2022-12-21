package zerobase.group2.cookingRecipe.member.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zerobase.group2.cookingRecipe.cache.CacheKey;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberRole;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final MailComponent mailComponent;

    private final RedisTemplate<String, String> redisTemplate;

    public MemberDto register(String email, String password, String name) {
        memberRepository.findById(email)
            .ifPresent(e -> {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
            });

        String uuid = UUID.randomUUID().toString();
        List<String> roles = new ArrayList<>();
        roles.add(MemberRole.PREFIX + MemberRole.USER);

        Member member = memberRepository.save(Member.builder()
            .email(email)
            .name(name)
            .password(hashedPassword(password, BCrypt.gensalt()))
            .emailAuthDue(LocalDateTime.now().plusHours(1))
            .emailAuthKey(uuid)
            .status(MemberStatus.BEFORE_AUTH)
            .roles(roles)
            .build());

        sendEmail(email, uuid);

        return MemberDto.from(member);
    }

    private void sendEmail(String email, String uuid) {
        String subject = "EZ Cooking Recipe의 회원이 되신 것을 축하드립니다.";
        String text = "<p>아래 링크를 클릭해서 이메일 인증을 완료하세요.</p>" +
            "<div><a target='_blank' href='http://localhost:8080/member/email-auth?key=" +
            uuid + "'> 이메일 인증 </a></div>";
        mailComponent.sendMail(email, subject, text);
    }

    private String hashedPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public void emailAuth(String key) {
        Member member = memberRepository.findByEmailAuthKey(key)
            .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_VALID));

        if (member.getStatus() != MemberStatus.BEFORE_AUTH) {
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        if (LocalDateTime.now().isAfter(member.getEmailAuthDue())){
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setStatus(MemberStatus.IN_USE);
        member.setEmailAuthDue(LocalDateTime.now());
        memberRepository.save(member);
    }

    public MemberDto getInfoById(String email) {
        return MemberDto.from(getMemberById(email));
    }

    private Member getMemberById(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public MemberDto editMemberInfo(String email, String name) {
        Member member = getMemberById(email);
        member.setName(name);
        memberRepository.save(member);
        return MemberDto.from(member);
    }

    public void editPassword(String email, String oldPassword, String newPassword) {
        Member member = getMemberById(email);

        if(member.validatePassword(hashedPassword(oldPassword, member.getPassword()))){
            throw new CustomException(ErrorCode.DATA_NOT_VALID);
        }

        member.setPassword(hashedPassword(newPassword, BCrypt.gensalt()));
        memberRepository.save(member);
    }

    public void withdraw(String email, String password) {
        Member member = getMemberById(email);

        if(member.validatePassword(hashedPassword(password, member.getPassword()))){
            throw new CustomException(ErrorCode.DATA_NOT_VALID);
        }

        member.setName("탈퇴회원");
        member.setStatus(MemberStatus.WITHDRAW);
        memberRepository.save(member);
    }

    public boolean sendEmailToResetPassword(String email) {
        Member member = getMemberById(email);

        if(member.getStatus() == MemberStatus.BEFORE_AUTH){
            throw new CustomException(ErrorCode.EMAIL_NOT_AUTHENTICATED);
        }

        if(member.validateKeyAndDue()){
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setPasswordResetKey(UUID.randomUUID().toString());
        member.setPasswordResetDue(LocalDateTime.now().plusMinutes(10));
        memberRepository.save(member);

        sendEmailToResetPassword(member.getEmail(), member.getEmailAuthKey());

        return true;
    }

    private void sendEmailToResetPassword(String email, String uuid) {
        String subject = "비밀번호 초기화 이메일";
        String text = "<p>아래 링크를 클릭해서 비밀번호를 재설정하세요.</p>" +
            "<div><a target='_blank' href='http://localhost:8080/member/reset-password?key=" +
            uuid + "'> 비밀번호 초기화 </a></div>";
        mailComponent.sendMail(email, subject, text);
    }

    public String authPasswordResetKey(String key) {
        Member member = memberRepository.findByPasswordResetKey(key)
            .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_VALID));

        if (LocalDateTime.now().isAfter(member.getPasswordResetDue())){
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setPasswordResetKey("");
        member.setPasswordResetDue(LocalDateTime.now());
        member.setStatus(MemberStatus.IN_USE);
        memberRepository.save(member);

        return member.getEmail();
    }

    public void processResetPassword(String email, String password) {
        Member member = getMemberById(email);

        member.setPassword(hashedPassword(password, BCrypt.gensalt()));
        memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getMemberById(email);
    }

    public Member attemptJwtReissue(String email, String refreshToken) {
        Member member = getMemberById(email);

        String tokenInCache = getRefreshToken(member.getEmail());

        if(!StringUtils.hasText(tokenInCache) || !refreshToken.equals(tokenInCache)){
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }

        return member;
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue()
            .get(CacheKey.REFRESH_TOKEN + "::" + email);
    }

    public void putRefreshToken(String email, String token){
        redisTemplate.opsForValue()
            .set(CacheKey.REFRESH_TOKEN + "::" + email, token, CacheKey.DEFAULT_EXPIRE_SEC, TimeUnit.SECONDS);
    }

    public String deleteRefreshToken(String email) {
        return redisTemplate.opsForValue()
            .getAndDelete(CacheKey.REFRESH_TOKEN + "::" + email);
    }

}
