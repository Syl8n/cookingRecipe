package zerobase.group2.cookingRecipe.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zerobase.group2.cookingRecipe.cache.CacheKey;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.like.repository.LikeRepository;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.entity.RefreshToken;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.repository.RefreshTokenRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LikeRepository likeRepository;
    private final RecipeRepository recipeRepository;
    private final MailComponent mailComponent;
    private final PasswordEncoder passwordEncoder;

    public MemberDto register(String email, String password, String name) {
        memberRepository.findById(email)
                .ifPresent(e -> {
                    throw new CustomException(
                            ErrorCode.EMAIL_ALREADY_REGISTERED);
                });

        String uuid = UUID.randomUUID().toString();

        Member member = memberRepository.save(Member.builder()
                .email(email)
                .name(name)
                .password(hashedPassword(password, BCrypt.gensalt()))
                .emailAuthDue(LocalDateTime.now().plusHours(1))
                .emailAuthKey(uuid)
                .status(MemberStatus.BEFORE_AUTH)
                .roles(new ArrayList<>())
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
                .orElseThrow(
                        () -> new CustomException(ErrorCode.DATA_NOT_VALID));

        if (member.getStatus() != MemberStatus.BEFORE_AUTH) {
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        if (LocalDateTime.now().isAfter(member.getEmailAuthDue())) {
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
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public MemberDto editMemberInfo(String email, String name) {
        Member member = getMemberById(email);
        member.setName(name);
        memberRepository.save(member);
        return MemberDto.from(member);
    }

    public void editPassword(String email, String oldPassword, String newPassword) {
        Member member = getMemberById(email);

        if (member.validatePassword(
                hashedPassword(oldPassword, member.getPassword()))) {
            throw new CustomException(ErrorCode.DATA_NOT_VALID);
        }

        member.setPassword(hashedPassword(newPassword, BCrypt.gensalt()));
        memberRepository.save(member);
    }

    public void withdraw(String email, String password) {
        Member member = getMemberById(email);

        if (member.validatePassword(
                hashedPassword(password, member.getPassword()))) {
            throw new CustomException(ErrorCode.DATA_NOT_VALID);
        }

        List<LikeEntity> likes = likeRepository.findAllByMember(member);
        List<Recipe> recipes = likes.stream().map(LikeEntity::getRecipe)
                .collect(Collectors.toList());
        for (Recipe recipe : recipes) {
            recipe.setLikeCount(recipe.getLikeCount() - 1);
        }
        recipeRepository.saveAll(recipes);
        likeRepository.deleteAll(likes);

        member.setName("탈퇴회원");
        member.setStatus(MemberStatus.WITHDRAW);
        memberRepository.save(member);
    }

    public boolean sendEmailToResetPassword(String email) {
        Member member = getMemberById(email);

        if (member.getStatus() == MemberStatus.BEFORE_AUTH) {
            throw new CustomException(ErrorCode.EMAIL_NOT_AUTHENTICATED);
        }

        if (member.validateKeyAndDue()) {
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setPasswordResetKey(UUID.randomUUID().toString());
        member.setPasswordResetDue(LocalDateTime.now().plusMinutes(10));
        memberRepository.save(member);

        sendEmailToResetPassword(member.getEmail(),
                member.getPasswordResetKey());

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
                .orElseThrow(
                        () -> new CustomException(ErrorCode.DATA_NOT_VALID));

        if (LocalDateTime.now().isAfter(member.getPasswordResetDue())) {
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setPasswordResetDue(LocalDateTime.now());
        member.setStatus(MemberStatus.IN_USE);
        memberRepository.save(member);

        return member.getEmail();
    }

    public void processResetPassword(String email, String password, String key) {
        Member member = getMemberById(email);

        if (!key.equals(member.getPasswordResetKey())) {
            throw new CustomException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setPassword(hashedPassword(password, BCrypt.gensalt()));
        member.setPasswordResetKey("");

        memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getMemberById(email);
    }

    public void validateJwtReissue(String tokenInCache, String refreshToken) {
        if (!StringUtils.hasText(tokenInCache) || !refreshToken.equals(
                tokenInCache)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }
    }

    @Cacheable(value = CacheKey.REFRESH_TOKEN, key = "#username")
    public String getRefreshToken(String username) {
        return getRefreshTokenById(username);
    }

    @CachePut(value = CacheKey.REFRESH_TOKEN, key = "#username")
    public String putRefreshToken(String username, String token) {
        return refreshTokenRepository.save(RefreshToken.builder()
                .username(username)
                .refreshToken(token)
                .build()).getRefreshToken();
    }

    @CacheEvict(value = CacheKey.REFRESH_TOKEN, key = "#username")
    public String deleteRefreshToken(String username) {
        String refreshToken = getRefreshTokenById(username);
        refreshTokenRepository.deleteById(username);
        return refreshToken;
    }

    private String getRefreshTokenById(String username) {
        return refreshTokenRepository.findById(username)
                .map(RefreshToken::getRefreshToken)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.TOKEN_NOT_VALID));
    }

    public Member authenticate(String username, String password) {
        Member member = getMemberById(username);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return member;
    }

    public List<String> getLikes(String email) {
        Member member = getMemberById(email);

        return member.getLikeEntityList().stream()
                .map(e -> e.getRecipe().getTitle())
                .collect(Collectors.toList());
    }
}
