package zerobase.group2.cookingRecipe.member.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister.Request;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.exception.MemberException;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.type.ErrorCode;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final MailComponent mailComponent;

    public MemberDto register(Request request) {
        memberRepository.findById(request.getEmail())
            .ifPresent(e -> {
                throw new MemberException(ErrorCode.EMAIL_ALREADY_REGISTERED);
            });

        String uuid = UUID.randomUUID().toString();

        Member member = memberRepository.save(Member.builder()
            .email(request.getEmail())
            .name(request.getName())
            .password(hashedPassword(request.getPassword()))
            .emailAuthDue(LocalDateTime.now().plusHours(1))
            .emailAuthKey(uuid)
            .emailAuthYn(false)
            .status(MemberStatus.BEFORE_AUTH)
            .build());

        sendEmail(request.getEmail(), uuid);

        return MemberDto.from(member);
    }

    private void sendEmail(String email, String uuid) {
        String subject = "EZ Cooking Recipe의 회원이 되신 것을 축하드립니다.";
        String text = "<p>아래 링크를 클릭해서 이메일 인증을 완료하세요.</p>" +
            "<div><a target='_blank' href='http://localhost:8080/member/email-auth?key=" +
            uuid + "'> 이메일 인증 </a></div>";
        mailComponent.sendMail(email, subject, text);
    }

    private String hashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean emailAuth(String key) {
        Member member = memberRepository.findByEmailAuthKey(key)
            .orElseThrow(() -> new MemberException(ErrorCode.DATA_NOT_VALID));

        if (member.isEmailAuthYn()) {
            throw new MemberException(ErrorCode.ACCESS_NOT_VALID);
        }

        if (LocalDateTime.now().isAfter(member.getEmailAuthDue())){
            throw new MemberException(ErrorCode.ACCESS_NOT_VALID);
        }

        member.setStatus(MemberStatus.IN_USE);
        member.setEmailAuthYn(true);
        member.setEmailAuthDue(LocalDateTime.now());
        memberRepository.save(member);

        return member.isEmailAuthYn();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));

//        validate(member);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

//        if (member.isAdminYn()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        }

        return new User(member.getEmail(), member.getPassword(), grantedAuthorities);
    }

}
