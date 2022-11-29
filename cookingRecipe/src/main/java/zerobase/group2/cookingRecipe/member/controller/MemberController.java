package zerobase.group2.cookingRecipe.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("member/register")
    public void memberRegister(@RequestBody @Valid MemberRegister.Request request){
        memberService.register(request);
    }

    @GetMapping("/member/email-auth")
    public void emailAuth(HttpServletRequest request) {

        String uuid = request.getParameter("id");

        memberService.emailAuth(uuid);
    }
}
