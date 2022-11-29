package zerobase.group2.cookingRecipe.member.service;

import static zerobase.group2.cookingRecipe.member.type.MemberError.DATA_NOT_VALID;
import static zerobase.group2.cookingRecipe.member.type.MemberError.EMAIL_ALREADY_REGISTERED;
import static zerobase.group2.cookingRecipe.member.type.MemberError.INTERNAL_SERVER_ERROR;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.monitor.os.OsStats.Mem;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.group2.cookingRecipe.member.component.MailComponent;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister.Request;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.exception.MemberException;
import zerobase.group2.cookingRecipe.member.repository.MemberRepository;
import zerobase.group2.cookingRecipe.member.type.MemberError;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MailComponent mailComponent;

    public void register(Request request) {
        memberRepository.findById(request.getEmail())
            .ifPresent(e -> {
                throw new MemberException(EMAIL_ALREADY_REGISTERED);
            });

        String uuid = UUID.randomUUID().toString();

        memberRepository.save(Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(hashedPassword(request.getPassword()))
                .emailAuthDue(LocalDateTime.now())
                .emailAuthKey(uuid)
                .emailAuthYn(false)
                .status(MemberStatus.BEFORE_AUTH)
                .build());

        sendEmail(request.getEmail(), uuid);
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

    public void emailAuth(String uuid) {
        Member member = memberRepository.findByEmailAuthKey(uuid)
            .orElseThrow(() -> new MemberException(DATA_NOT_VALID));

        if (member.isEmailAuthYn()) {
            throw new MemberException(INTERNAL_SERVER_ERROR);
        }

        if (LocalDateTime.now().isAfter(member.getEmailAuthDue())){
            throw new MemberException(INTERNAL_SERVER_ERROR);
        }

        member.setStatus(MemberStatus.IN_USE);
        member.setEmailAuthYn(true);
        member.setEmailAuthDue(LocalDateTime.now());
        memberRepository.save(member);
    }
}
